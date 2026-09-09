package zhangwei.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One line of the conversation: a speech bubble beside the speaker's picture.
 *
 * <p>Both speakers share this class because their bubbles differ only in which
 * side they sit on, which {@link #flip()} takes care of.
 */
public class DialogBox extends HBox {

    /** Half the width of the speaker's picture, used to round it off. */
    private static final double PICTURE_RADIUS = 24.0;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a bubble holding the given text next to the given picture.
     *
     * @param text what the speaker said.
     * @param image the speaker's picture.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            // A dialog box is its own root, so it is both the thing being
            // loaded and the thing that handles the loaded controls.
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load a dialog box.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        roundOff(displayPicture);
    }

    /**
     * Returns a bubble for something the user typed, shown on the right.
     *
     * @param text what the user typed.
     * @param image the user's picture.
     * @return the bubble, ready to be added to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a bubble for something the chatbot said, shown on the left.
     *
     * @param text what the chatbot said.
     * @param image the chatbot's picture.
     * @return the bubble, ready to be added to the conversation.
     */
    public static DialogBox getZhangWeiDialog(String text, Image image) {
        DialogBox box = new DialogBox(text, image);
        box.flip();
        return box;
    }

    /**
     * Moves the picture to the left of the text and lines the bubble up on
     * that side, so the chatbot's replies face the user's messages.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().remove("dialog-label");
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Clips the given picture to a circle, which reads as a portrait rather
     * than as a photograph someone pasted in.
     *
     * @param picture the picture to round off.
     */
    private static void roundOff(ImageView picture) {
        Circle clip = new Circle(PICTURE_RADIUS, PICTURE_RADIUS, PICTURE_RADIUS);
        picture.setClip(clip);
    }
}
