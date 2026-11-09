package com.mimoneda.app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int ITERACIONES = 65_536;
    private static final int LONGITUD_CLAVE_BITS = 256;
    private static final int LONGITUD_SALT_BYTES = 16;

    private PasswordUtils() {
    }

    public static Credenciales crearCredenciales(String password) {
        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        new SecureRandom().nextBytes(salt);

        byte[] hash = generarHash(password, salt);
        return new Credenciales(aHex(hash), aHex(salt));
    }

    public static boolean verificar(
            String password,
            String hashEsperadoHex,
            String saltHex
    ) {
        byte[] hashEsperado = desdeHex(hashEsperadoHex);
        byte[] salt = desdeHex(saltHex);
        byte[] hashCalculado = generarHash(password, salt);
        return MessageDigest.isEqual(hashEsperado, hashCalculado);
    }

    private static byte[] generarHash(String password, byte[] salt) {
        PBEKeySpec especificacion = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERACIONES,
                LONGITUD_CLAVE_BITS
        );

        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return fabrica.generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException("PBKDF2 no está disponible", excepcion);
        } catch (Exception excepcion) {
            throw new IllegalStateException("No se pudo proteger la contraseña", excepcion);
        } finally {
            especificacion.clearPassword();
        }
    }

    private static String aHex(byte[] datos) {
        StringBuilder resultado = new StringBuilder(datos.length * 2);
        for (byte dato : datos) {
            resultado.append(String.format("%02x", dato & 0xff));
        }
        return resultado.toString();
    }

    private static byte[] desdeHex(String hexadecimal) {
        if (hexadecimal == null || hexadecimal.length() % 2 != 0) {
            throw new IllegalArgumentException("Valor hexadecimal inválido");
        }

        byte[] resultado = new byte[hexadecimal.length() / 2];
        for (int i = 0; i < hexadecimal.length(); i += 2) {
            int parteAlta = Character.digit(hexadecimal.charAt(i), 16);
            int parteBaja = Character.digit(hexadecimal.charAt(i + 1), 16);
            if (parteAlta < 0 || parteBaja < 0) {
                throw new IllegalArgumentException("Valor hexadecimal inválido");
            }
            resultado[i / 2] = (byte) ((parteAlta << 4) + parteBaja);
        }
        return resultado;
    }

    public static final class Credenciales {

        private final String hash;
        private final String salt;

        private Credenciales(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash() {
            return hash;
        }

        public String getSalt() {
            return salt;
        }
    }
}
