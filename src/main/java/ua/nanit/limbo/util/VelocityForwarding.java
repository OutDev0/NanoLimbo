package ua.nanit.limbo.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;

@UtilityClass
public class VelocityForwarding {

    private static final String ALGORITHM = "HmacSHA256";

    public static final byte MAX_SUPPORTED_FORWARDING_VERSION = 1;

    public static boolean checkVelocityKeyIntegrity(@NonNull ClientConnection conn, @NonNull ByteMessage buf) {
        byte[] signature = new byte[32];
        buf.readBytes(signature);

        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);

        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(conn.getServer().getConfig().getInfoForwarding().getSecretKey(), ALGORITHM));
            byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature)) {
                return false;
            }
        } catch (InvalidKeyException | java.security.NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        return true;
    }

}
