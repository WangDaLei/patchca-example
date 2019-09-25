import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.color.RandomColorFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.*;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.AbstractCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.RandomWordFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

import static java.lang.Thread.sleep;

public class CaptchaExample {

    private static class MyCaptchaService extends AbstractCaptchaService {

        MyCaptchaService() {
            this.wordFactory = new MyWordFactory();
            this.fontFactory = new RandomFontFactory();
            ((RandomFontFactory) this.fontFactory).setMaxSize(52);
            ((RandomFontFactory) this.fontFactory).setMinSize(32);
            this.textRenderer = new BestFitTextRenderer();
            this.backgroundFactory = new SingleColorBackgroundFactory();
//            this.colorFactory = new SingleColorFactory(new Color(25, 60, 170));
            this.colorFactory = new MyColorFactory();
            CurvesRippleFilterFactory crff = new CurvesRippleFilterFactory(colorFactory);
            DoubleRippleFilterFactory drff = new DoubleRippleFilterFactory();
            WobbleRippleFilterFactory wrff = new WobbleRippleFilterFactory();
            DiffuseRippleFilterFactory dirff = new DiffuseRippleFilterFactory();
            MarbleRippleFilterFactory mrff = new MarbleRippleFilterFactory();
            Random random = new Random();
            switch (random.nextInt(5)) {
                case 0:
                    this.filterFactory = crff;
                    break;
                case 1:
                    this.filterFactory = drff;
                    break;
                case 2:
                    this.filterFactory = wrff;
                    break;
                case 3:
                    this.filterFactory = dirff;
                    break;
                case 4:
                    this.filterFactory = mrff;
                    break;
            }
            this.width = 160;
            this.height = 70;
        }
        private class MyWordFactory extends RandomWordFactory {
            public MyWordFactory() {
                //文本范围和长度
                characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                minLength = 4;
                maxLength = 4;
            }
        }
        private class MyColorFactory implements ColorFactory {
            @Override
            public Color getColor(int x) {
                //颜色
                int[] c = new int[3];
                Random random = new Random();
                for (int fi = 0; fi < c.length; fi++) {
                        c[fi] = random.nextInt(256);
                }
                Color color = new Color(c[0], c[1], c[2]);
                return color;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int count = 1;
        while (count <= 200000) {
            String patchca = null;
            try (OutputStream os = new FileOutputStream("./images/captcha" + String.valueOf(count) + ".png")) {
                patchca = EncoderHelper.getChallangeAndWriteImage(new MyCaptchaService(), "png", os);
                System.out.println(patchca);
            }
            File old_file = new File("./images/captcha" + String.valueOf(count) + ".png");
            File new_file = new File("./images/" + patchca + "_" + String.valueOf(count) + ".jpg");
            old_file.renameTo(new_file);
            count += 1;
        }
    }

}