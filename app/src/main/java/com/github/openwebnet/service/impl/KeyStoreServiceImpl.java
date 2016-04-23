package com.github.openwebnet.service.impl;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

public class KeyStoreServiceImpl implements KeyStoreService {

    private static final Logger log = LoggerFactory.getLogger(KeyStoreService.class);

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TYPE_RSA = "RSA";
    private static final String SIGNATURE_SHA512withRSA = "SHA512withRSA";
    private static final int KEYS_VALIDITY_YEARS = 999;

    @Inject
    Context mContext;

    public KeyStoreServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public byte[] getKey() {
        // TODO
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        log.debug("key: {}", BaseEncoding.base16().lowerCase().encode(key));
        return key;
    }

    @Override
    public void writeKeyToFile(String fileName) throws IOException {
        File file = new File(mContext.getFilesDir(), fileName);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(BaseEncoding.base16().lowerCase().encode(getKey()).getBytes());
        stream.close();
    }

    /*
     * Creates a public and private key and stores it using the Android Key Store,
     * so that only this application will be able to access the keys.
     * Uses deprecated KeyPairGeneratorSpec because KeyGenParameterSpec is available only on API 23.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void createKeys(String keyAlias) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // create a start and end time, for the validity range of the key pair that's about to be generated
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, KEYS_VALIDITY_YEARS);

        // the KeyPairGeneratorSpec object is how parameters for your key pair are passed to the KeyPairGenerator
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mContext)
            // use the alias later to retrieve the key, it's a key for the key!
            .setAlias(keyAlias)
            // the subject used for the self-signed certificate of the generated pair
            .setSubject(new X500Principal("CN=" + keyAlias))
            // the serial number used for the self-signed certificate of the generated pair
            .setSerialNumber(BigInteger.valueOf(1337))
            // date range of validity for the generated pair.
            .setStartDate(start.getTime())
            .setEndDate(end.getTime())
            .build();

        // initialize a KeyPair generator using an algorithm
        KeyPairGenerator kpGenerator = KeyPairGenerator
                .getInstance(TYPE_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        kpGenerator.initialize(spec);
        KeyPair kp = kpGenerator.generateKeyPair();
        log.debug("Public Key is: " + kp.getPublic().toString());
    }

    /*
     * Signs the data using the key pair stored in the Android Key Store.
     * This signature can be used with the data later to verify it was signed by this application.
     *
     * @return A string encoding of the data signature generated
     */
    private String signData(String keyAlias, String inputStr) throws KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException, CertificateException {

        // load keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // weird artifact of Java API: if you don't have an InputStream to load,
        // you still need to call "load" or it'll crash
        ks.load(null);

        // load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(keyAlias, null);

        /* If the entry is null, keys were never stored under this alias.
         * Debug steps in this situation would be:
         * - check the list of aliases by iterating over Keystore.aliases(), be sure the alias exists.
         * - if that's empty, verify they were both stored and pulled from the same keystore "AndroidKeyStore"
         */
        if (entry == null) {
            log.warn("No key found under alias: " + keyAlias);
            log.warn("Exiting signData()...");
            return null;
        }

        /* If entry is not a KeyStore.PrivateKeyEntry, it might have gotten stored in a previous
         * iteration of your application that was using some other mechanism, or been overwritten
         * by something else using the same keystore with the same alias.
         * You can determine the type using entry.getClass() and debug from there.
         */
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            log.warn("Not an instance of a PrivateKeyEntry");
            log.warn("Exiting signData()...");
            return null;
        }

        // this class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using the specified algorithm.
        Signature s = Signature.getInstance(SIGNATURE_SHA512withRSA);

        // initialize Signature using specified private key
        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());

        // sign the data, store the result as a Base64 encoded String.
        s.update(inputStr.getBytes());
        byte[] signature = s.sign();
        String result = Base64.encodeToString(signature, Base64.DEFAULT);
        return result;
    }

    /*
     * Given some data and a signature, uses the key pair stored in the Android Key Store to verify
     * that the data was signed by this application, using that key pair.
     *
     * @param input        The data to be verified.
     * @param signatureStr The signature provided for the data.
     * @return A boolean value telling you whether the signature is valid or not.
     */
    private boolean verifyData(String keyAlias, String input, String signatureStr) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException {

        byte[] signature;

        // make sure the signature string exists. If not, bail out, nothing to do
        if (signatureStr == null) {
            log.warn("Invalid signature.");
            log.warn("Exiting verifyData()...");
            return false;
        }

        try {
            // the signature is going to be examined as a byte array, not as a base64 encoded string
            signature = Base64.decode(signatureStr, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            // signatureStr wasn't null, but might not have been encoded properly
            // it's not a valid Base64 string
            return false;
        }

        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // weird artifact of Java API: if you don't have an InputStream to load,
        // you still need to call "load" or it'll crash
        ks.load(null);

        // load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(keyAlias, null);

        if (entry == null) {
            log.warn("No key found under alias: " + keyAlias);
            log.warn("Exiting verifyData()...");
            return false;
        }

        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            log.warn("Not an instance of a PrivateKeyEntry");
            return false;
        }

        // this class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using the specified algorithm.
        Signature s = Signature.getInstance(SIGNATURE_SHA512withRSA);

        // Verify the data.
        s.initVerify(((KeyStore.PrivateKeyEntry) entry).getCertificate());
        s.update(input.getBytes());
        return s.verify(signature);
    }

}
