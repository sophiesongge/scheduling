/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.authentication.crypto;

import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


/**
 * Symmetric cryptography utilities for Key generation, encryption and decryption
 * <p>
 * Refer to the <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/jce/JCERefGuide.html">Java Cryptography Extension Reference Guide</a> at
 * to determine which parameters are best for key algorithm, key size and cipher;
 * although "AES", 128 and "AES" should be good enough in most
 * cases.
 * 
 * @author The ProActive Team
 * @since ProActive Scheduling 1.1
 * 
 */
public class KeyUtil {

    private static SecureRandom secureRandom;

    public static synchronized SecureRandom getSecureRandom() {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
            final byte[] dummy = new byte[512];
            secureRandom.nextBytes(dummy);
        }

        return secureRandom;
    }

    /**
     * Generates a secret symmetric key
     * 
     * @param algorithm algorithm used for key generation, ie AES
     * @param size size of the generated key, must be one of 128, 192, 256. Use 128 when unsure,
     *             default configurations and providers should refuse to use longer keys.
     * @return the generated key
     * @throws KeyException key generation or saving failed
     */
    public static synchronized SecretKey generateKey(String algorithm, int size) throws KeyException {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyException("Cannot initialize key generator", e);
        }

        keyGen.init(size, getSecureRandom());

        return keyGen.generateKey();
    }

    /**
     * Encrypt a message using a symmetric key
     * 
     * @param key secret key used for encryption
     * @param cipherParams cipher parameters: transformations, ie AES
     * @param message the message to encrypt
     * @return the encrypted message
     * @throws KeyException encryption failed, public key recovery failed
     */
    public static synchronized byte[] encrypt(SecretKey key, String cipherParams, byte[] message) throws KeyException {
        Cipher ciph = null;
        try {
            ciph = Cipher.getInstance(cipherParams);
            ciph.init(Cipher.ENCRYPT_MODE, key, getSecureRandom());
        } catch (Exception e) {
            throw new KeyException("Coult not initialize cipher", e);
        }

        byte[] res = null;
        try {
            res = ciph.doFinal(message);
        } catch (Exception e) {
            throw new KeyException("Could not encrypt message", e);
        }
        return res;
    }

    /**
     * Decrypt a message using a symmetric key
     * 
     * @param key secret key used for decryption
     * @param cipherParams cipher parameters: transformations, ie AES
     * @param message the encrypted message
     * @return the decrypted message
     * @throws KeyException private key recovery failed, decryption failed
     */
    public static synchronized byte[] decrypt(SecretKey key, String cipherParams, byte[] message) throws KeyException {
        Cipher ciph = null;
        try {
            ciph = Cipher.getInstance(cipherParams);
            ciph.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeyException("Coult not initialize cipher", e);
        }

        byte[] res = null;
        try {
            res = ciph.doFinal(message);
        } catch (Exception e) {
            throw new KeyException("Could not decrypt message", e);
        }
        return res;
    }

}
