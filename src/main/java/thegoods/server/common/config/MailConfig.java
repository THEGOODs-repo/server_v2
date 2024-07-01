package thegoods.server.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class MailConfig {
    private final JavaMailSender javaMailSender;

    public boolean sendMail(String ToEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(new InternetAddress("khjoon8010@gmail.com", "TheGoods", "UTF-8"));
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject("TheGoods 인증번호 "); // 제목
            helper.setTo(ToEmail); // 받는사람

            String verificationCode = code;
            String emailBody = String.format("TheGoods 인증번호는 %s입니다.", verificationCode);

            helper.setText(emailBody, true);

            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
