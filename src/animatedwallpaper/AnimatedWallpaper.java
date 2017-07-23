package animatedwallpaper;

import java.awt.Desktop;
import java.io.File;
import java.util.concurrent.locks.LockSupport;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.UtilFX;

/**
 *
 * @author Sourabh Bhat <sourabh.bhat@iitb.ac.in>
 */
public class AnimatedWallpaper extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Sourabh's Desktop");

        primaryStage.getIcons().addAll(
                new Image(getClass().getResource("/images/desktop_32x32.png").toString())
        );

        File file = new File(getClass().getResource("/media/desktop.mp4").toString());
        System.out.println(file);
        String url = file.toString();

        System.out.println(url);
        Media media = new Media(url);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(screenBounds.getWidth());
        mediaView.setFitHeight(screenBounds.getHeight());

        Image desktopIconImage = new Image(
                getClass().getResource("/images/folders-desktop.png").toString(),
                100, 110, true, true);
        ImageView desktopIcon = new ImageView(desktopIconImage);
        desktopIcon.setOnMouseEntered(me -> desktopIcon.setEffect(new Glow()));
        desktopIcon.setOnMouseExited(me -> desktopIcon.setEffect(null));
        desktopIcon.setX(80);
        desktopIcon.setY(80);
        desktopIcon.setOnMouseClicked(me -> {
            if (me.getClickCount() == 2) {
                openDesktopFolder();
            }
        });
        UtilFX.dragUsingMouse(desktopIcon);

        Group root = new Group(mediaView, desktopIcon);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight(), Color.TRANSPARENT);
        primaryStage.setScene(scene);

        primaryStage.show();
        primaryStage.toBack();
        primaryStage.setOnCloseRequest(we -> we.consume());

//        primaryStage.focusedProperty()
//                .addListener((observable, wasFocused, isNowFocused) -> {
//                    if (!wasFocused && isNowFocused) {
//                        System.out.println("Is focused moving to back.");
//                        primaryStage.toBack();
//                    }
//                });
        new Thread(() -> {
            while (true) {
                LockSupport.parkNanos(1_000_000_000);
                Platform.runLater(() -> primaryStage.toBack());
            }
        }).start();
    }

    static void openDesktopFolder() {
        new Thread(() -> {
            try {
                Desktop desktop = Desktop.getDesktop();
                System.out.println(new File(System.getProperty("user.home") + "/Desktop"));
                desktop.open(new File(System.getProperty("user.home") + "/Desktop"));
            } catch (Exception ex) {
                // Ignore
            }
        }).start();
    }
}
