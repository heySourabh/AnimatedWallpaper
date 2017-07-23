package animatedwallpaper;

import java.awt.Desktop;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.LockSupport;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
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

        JWindow window = new JWindow();
        JFXPanel fXPanel = new JFXPanel();
        window.add(fXPanel);
        window.setSize((int) screenBounds.getWidth(), (int) screenBounds.getHeight());
        window.setIconImage(new ImageIcon(getClass().getResource("/images/desktop_32x32.png")).getImage());

        InputStream fileStream = getClass().getResourceAsStream("/media/desktop.mp4");
        Path tmpFile = Files.createTempFile("", ".mp4");
        Files.copy(fileStream, tmpFile, StandardCopyOption.REPLACE_EXISTING);
        File file = tmpFile.toFile();
        System.out.println(file);
        String url = file.toURI().toString();

        System.out.println(url);
        Media media = new Media(url);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(screenBounds.getWidth());
        mediaView.setFitHeight(screenBounds.getHeight());

        MenuItem playPauseItem = new MenuItem("Pause / Pause");
        playPauseItem.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.play();
            }
        });
        ContextMenu menu = new ContextMenu(playPauseItem);
        mediaView.setOnMouseClicked(me -> {
            if (me.getButton() == MouseButton.SECONDARY) {
                menu.show(mediaView, me.getScreenX(), me.getScreenY());
            }
        });

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

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        fXPanel.setScene(scene);

        window.setVisible(true);
        window.toBack();

        new Thread(() -> {
            while (true) {
                LockSupport.parkNanos(500_000_000);
                window.toBack();
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
