package be.ordina.unitils.testing.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author Filip Neven
 */
class MessageFormatUtil {

    static String formatMessage(String suppliedMessage, String specificMessage) {
        if (StringUtils.isEmpty(suppliedMessage)) {
            return specificMessage;
        } else {
            return suppliedMessage + "\n" + specificMessage;
        }
    }

}
