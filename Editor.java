package editor;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.*;
import java.util.*;


public class Editor extends Application {
    /**
     * An EventHandler to handle keys that get pressed.
     */
    private static String inputFilename = "";
    private static LinkedList<Text> inputText = new LinkedList<>(); // KEY DATA STRUCTURE!!!!!
    static Scene scene;

    private static int fontSize = 12;
    private static String fontName = "Verdana";
    private static int cursorIndex = 0; // holds the index the cursor is after in the linked list
    static double marginLeft = 5;
    static double marginTop = 0;
    static double pastWidths = marginLeft; // stores all preceding widths of elements in data structure in current line.
    static double pastHeights = marginTop; // stores height of all preceding lines of text.
    static double cursorPosX = marginLeft; // stores current X position of cursor relative to scene. NEVER < margin or > window width.
    static double cursorPosY = marginTop; // stores current Y position of cursor relative to scene. NEVER < 0.
    static Group root = new Group();
    private static final Rectangle cursor = new Rectangle(cursorPosX, cursorPosY);
    private static boolean debug = false;
    private static boolean readFileMode = false;
    private static Stack<ChangeSaver> changeLog = new Stack<>(); // <-- this will record all the add/delete actions
    private static Stack<ChangeSaver> undoLog = new Stack<>(); // <-- this will record all the actions that the user undoes.

    private class ChangeSaver {
        public String action;
        public double cursorX;
        public double cursorY;
        public int index;
        public Text text;

        public ChangeSaver(String a, double cX, double cY, int i, Text t) {
            action = a;
            cursorX = cX;
            cursorY = cY;
            index = i;
            text = t;
        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {
        /**
         * The Text to display on the screen.
         */
        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {

            root.getChildren().add(cursor); // add the cursor to the root.
            cursor.setX(cursorPosX);
            cursor.setY(cursorPosY);
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            /*pressing shortcut+s should save the text in your editor to the given file,
             replacing any existing text in the file
             */
            if (keyEvent.isShortcutDown()) {
                if (keyEvent.getCode() == KeyCode.P) {
                    // user pressed command P, which prints cursor position
                    System.out.println((int) cursorPosX + ", " + (int) cursorPosY);
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.Z) {
                    // undo function
                    if (debug) {
                        System.out.println("========UNDO CALLED.========");
                    }
                    undoFunction();
                    keyEvent.consume();

                } else if (keyEvent.getCode() == KeyCode.Y) {
                    // redo function
                    if (debug) {
                        System.out.println("========REDO CALLED.========");
                    }
                    redoFunction();
                    keyEvent.consume();

                } else if (keyEvent.getCode() == KeyCode.S) {
                    // save function.
                    saveFile();
                } else if (keyEvent.getCode() == KeyCode.EQUALS) {
                    //change font size 4 pixels up.
                    if (debug) {
                        System.out.println("FONT SIZE INCREASE: " + fontSize + " --> " + (fontSize + 4));
                    }
                    fontSize += 4;
                    if (inputText.size() > 0) {
                        resizeText();
                    }
                    keyEvent.consume();
                } else if (keyEvent.getCode() == KeyCode.MINUS) {
                    //change font size 4 pixels down
                    if (debug) {
                        System.out.println("FONT SIZE DECREASE: " + fontSize + " --> " + (fontSize - 4));
                    }
                    if (fontSize > 8) {
                        fontSize -= 4;
                        if (inputText.size() > 0) {
                            resizeText();
                        }
                        keyEvent.consume();
                    }
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                double oldCX = cursorPosX;
                double oldCY = cursorPosY;
                // Use the KEY_TYPED event rather than KEY_PRESSED for letter keys, because with
                // the KEY_TYPED event, javafx handles the "Shift" key and associated
                // capitalization.
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace key, which is
                    // represented as a character of value = 8 on Windows.
                    if (cursorIndex >= 0 && cursorIndex <= inputText.size()) {
                        //create new text object w/ pastwidths & pastheights
                        Text newText = new Text(pastWidths, pastHeights, characterTyped);
                        newText.setTextOrigin(VPos.TOP);
                        newText.setFont(Font.font(fontName, fontSize));
                        root.getChildren().add(newText);//display new text object in scene
                        if (cursorIndex == inputText.size()) {
                            //if cursor is at end of text
                            inputText.add(newText); //add new text object to end of LL
                            cursorIndex += 1; // increment cursorindex
                            if (cursorPosX >= scene.getWidth() - newText.getLayoutBounds().getWidth() - marginLeft) {
                                //	If cursorposX is at end of line
                                if (characterTyped.equals(" ")) {
                                    // wrapping on a space is allowed.
                                    pastWidths = marginLeft;
                                    pastHeights += newText.getLayoutBounds().getHeight();
                                } else {
                                    //iterate through all previous entries in list to find a space. if no space found, wrap text at end of line.
                                    if (debug) {
                                        System.out.println("searching for space to perform word wrap.");
                                    }
                                    boolean foundSpace = false;
                                    Text cur2 = newText;
                                    Double pastWidthsC = pastWidths;
                                    Double pastHeightsC = pastHeights;
                                    int cICopy = cursorIndex;
                                    int ind = cursorIndex;
                                    while ((!foundSpace) && ind > 0) {
                                        if (cur2.getText().equals(" ")) {
                                            // space is found, wrap text here.
                                            pastWidths = marginLeft;
                                            pastHeights += cur2.getLayoutBounds().getHeight();
                                            foundSpace = true;
                                            cursorIndex = ind;
                                            updateText();
                                        }
                                        ind -= 1;
                                        cur2 = inputText.get(ind);
                                    }
                                    if (!foundSpace) {
                                        pastWidths = marginLeft;
                                        pastHeights += cur2.getLayoutBounds().getHeight();
                                    } else {
                                        cursorIndex = cICopy;
                                        pastWidths = inputText.get(cICopy - 1).getLayoutBounds().getWidth() + inputText.get(cICopy - 1).getX();
                                        pastHeights = inputText.get(cICopy - 1).getY();
                                    }
                                }
                            } else {
                                //else if cursor is not at end of line:
                                pastWidths += newText.getLayoutBounds().getWidth();
                            }
                            //set cursor position X,Y to pastwidths, pastheights
                            cursorPosX = pastWidths;
                            cursorPosY = pastHeights;
                            cursor.setX(cursorPosX);
                            cursor.setY(cursorPosY);
                        } else {
                            // else if cursor is not at end of text
                            inputText.add(cursorIndex, newText); // add new text object to linked list at cursorindex
                            cursorIndex += 1;
                            if (cursorPosX >= scene.getWidth() - newText.getLayoutBounds().getWidth() - marginLeft) {
                                pastWidths = marginLeft;
                                pastHeights += newText.getLayoutBounds().getHeight();
                            } else {
                                //else if cursor is not at end of line:
                                pastWidths += newText.getLayoutBounds().getWidth();
                            }
                            //set cursor position X,Y to pastwidths, pastheights
                            cursorPosX = pastWidths;
                            cursorPosY = pastHeights;
                            cursor.setX(cursorPosX);
                            cursor.setY(cursorPosY);
                            //update all indices of linked list after cursorindex, including cursorindex
                            updateText();
                        }
                        if (debug) {
                            System.out.println(characterTyped + " added.");
                            printLinkedList();
                            printRoot();
                            System.out.println("Cursor index is " + cursorIndex);
                        }
                        LinkedList<Text> llCOPY = (LinkedList<Text>) inputText.clone();
                        changeLog.add(new ChangeSaver("ADD", oldCX, oldCY, cursorIndex, newText));
                    }
                    keyEvent.consume();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                // Arrow keys should be processed using the KEY_PRESSED event, because KEY_PRESSED
                // events have a code that we can check (KEY_TYPED events don't have an associated
                // KeyCode).
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.ENTER) {
                    if (debug) {
                        System.out.println("KeyPressed: enter key pressed.");
                    }
                    // this means the enter key is pressed; insert a character "\n" into the list, and handle accordingly.
                    cursorIndex += 1;
                    if (debug) {
                        System.out.println("cursor index is " + cursorIndex);
                    }
                    if (cursorIndex >= inputText.size()) {
                        // if cursor is at end of text, then just add text to end, no need to update rest.
                        inputText.add(new Text(pastWidths, pastHeights, "\n"));
                    } else {
                        // if cursor is not at end of text, add text to cursorIndex-1, and update rest.
                        inputText.add(cursorIndex - 1, new Text(pastWidths, pastHeights, "\n"));
                        updateText();
                    }
                    if (debug) {
                        printLinkedList();
                    }
                    Text lastTyped = inputText.get(cursorIndex - 2);
                    pastWidths = marginLeft;
                    pastHeights += lastTyped.getLayoutBounds().getHeight();
                    cursorPosX = pastWidths;
                    cursorPosY = pastHeights;
                    cursor.setX(cursorPosX);
                    cursor.setY(cursorPosY);
                }

                if (code == KeyCode.BACK_SPACE) {
                    // deletes the entry at cursorIndex, re-renders in frame.
                    double oldCX = cursorPosX;
                    double oldCY = cursorPosY;
                    if (inputText.size() > 0 && cursorIndex > 0) {
                        // if the size of linked list is not zero & cursor is not at beginning of all text
                        System.out.println("deleting inputText(" + (cursorIndex - 1) + "), which is " + inputText.get(cursorIndex - 1).getText());
                        Text removedText = inputText.remove(cursorIndex - 1);
                        if (debug) {
                            System.out.println("deleting " + removedText.getText());
                        }
                        cursorIndex -= 1; // KEY THING HERE : PAY ATTENTION TO IT
                        if (debug) {
                            System.out.println("cursor index is " + cursorIndex);
                            System.out.println("removing root(" + (cursorIndex) + ") -->" + root.getChildren().get(cursorIndex).toString() + "");
                            printLinkedList();
                            printRoot();
                        }
                        javafx.scene.Node thing = root.getChildren().remove(cursorIndex + 1);
                        if (debug) {
                            System.out.println("DELETING FROM ROOT --> " + ((Text) thing).getText());
                            System.out.print("root width of text: " + ((Text) thing).getLayoutBounds().getWidth());
                            System.out.println(", and LL width of text: " + removedText.getLayoutBounds().getWidth());
                        }

                        // because first entry is not part of LL and cursorIndex has been has been decremented
                        if (cursorPosX > marginLeft) {
                            // if the cursor is not at the beginning of the line, decrement cursorposx and pastwidths.
                            if (debug) {
                                System.out.println("cursor is not at beginning of line");
                            }
                            cursorPosX -= removedText.getLayoutBounds().getWidth();
                            cursor.setX(cursorPosX);
                            pastWidths -= removedText.getLayoutBounds().getWidth();
                        } else if (cursorPosX <= marginLeft || removedText.getText().equals("\n")
                                || removedText.getText().equals("\r\n")) {
                            // if cursor is at beginning of line
                            if (debug) {
                                System.out.println("cursor is at beginning of line");
                                System.out.println("Changing cursorX from " + cursorPosX + " to " +
                                        (inputText.get(cursorIndex - 1).getX() + inputText.get(cursorIndex - 1).getLayoutBounds().getWidth()));
                                System.out.println("Changing cursorY from " + cursorPosY + " to " +
                                        "" + (cursorPosY - removedText.getLayoutBounds().getHeight()));
                            }
                            cursorPosX = inputText.get(cursorIndex - 1).getX() + inputText.get(cursorIndex - 1).getLayoutBounds().getWidth();
                            cursorPosY -= removedText.getLayoutBounds().getHeight();
                            cursor.setX(cursorPosX);
                            cursor.setY(cursorPosY);
                            pastWidths = cursorPosX;
                            pastHeights = cursorPosY;
                        }
                        keyEvent.consume();
                        updateText();
                        changeLog.add(new ChangeSaver("DELETE", oldCX, oldCY, cursorIndex, removedText));
                    }

                } else if (code == KeyCode.LEFT) {
                    // move the cursor back one space if possible.
                    if (inputText.size() > 0 && cursorIndex > 0) {
                        // if cursor is not at beginning of text & size of linked list is not 0
                        cursorIndex -= 1;
                        if (cursorPosX <= marginLeft || inputText.get(cursorIndex).getText().equals("\n")
                                ) {
                            //	if cursor x position is at beginning of line: (== marginleft)
                            //set cursor X value to linkedlist.get(cursorindex).getX+ linkedlist.get(cursorIndex).getWidth
                            cursorPosX = inputText.get(cursorIndex).getX() +
                                    inputText.get(cursorIndex).getLayoutBounds().getWidth();
                            //set cursor Y value to cursorYValue - linkedlist.get(cursorindex).getHeight
                            cursorPosY -= inputText.get(cursorIndex).getLayoutBounds().getHeight();
                            if (debug) {
                                System.out.println("cursor index is " + cursorIndex);
                                System.out.println("LL[cursorIndex] is " + inputText.get(cursorIndex).getText());
                            }
                            pastWidths = cursorPosX;
                            pastHeights = cursorPosY;
                            cursor.setX(cursorPosX);
                            cursor.setY(cursorPosY);
                        } else {
                            //	else if cursor is not at beginning of line:
                            cursorPosX -= inputText.get(cursorIndex).getLayoutBounds().getWidth();
                            if (debug) {
                                System.out.println("cursor index is " + (cursorIndex));
                                System.out.println("LL[cursorIndex] is " + inputText.get(cursorIndex).getText());
                            }
                            pastWidths = cursorPosX;
                            cursor.setX(cursorPosX);
                        }
                    }
                } else if (code == KeyCode.RIGHT) {
                    // move the cursor forwards one space if possible.
                    if (cursorIndex < inputText.size() && inputText.size() > 0) { // if cursor is not at end of text
                        cursorIndex += 1;
                        if (cursorPosX >= scene.getWidth() -
                                inputText.get(cursorIndex - 1).getLayoutBounds().getWidth() - marginLeft
                                || (inputText.get(cursorIndex - 1).getText().equals("\n"))
                                || (inputText.get(cursorIndex - 1).getText().equals("\r\n"))) {
                            // if cursor is at end of line (because of word wrap or enter)
                            // cursor.setX(cursor.getX() + (fontSize / widthFactor));
                            // set cursor X value to marginLeft
                            cursorPosX = marginLeft;
                            // increment cursor Y value by linkedlist.get(cursorIndex).getHeight
                            cursorPosY += inputText.get(cursorIndex).getLayoutBounds().getHeight();
                            if (debug) {
                                System.out.println("cursor index is " + cursorIndex);
                                System.out.println("LL[cursorIndex] is " + inputText.get(cursorIndex).getText());
                            }
                            // set pastwidths to cursor x value
                            pastWidths = cursorPosX;
                            // set pastheights to cursor y value
                            pastHeights = cursorPosY;
                            cursor.setX(cursorPosX);
                            cursor.setY(cursorPosY);
                        } else {
                            // if cursor is not at end of line
                            // increment cursor x value by linkedlist.get(cursorindex).getwidth
                            cursorPosX += inputText.get(cursorIndex - 1).getLayoutBounds().getWidth();
                            if (debug) {
                                System.out.println("cursor index is " + (cursorIndex));
                                System.out.println("LL[cursorIndex] is " + inputText.get(cursorIndex - 1).getText());
                            }
                            // set pastwidths to cursor x value
                            pastWidths = cursorPosX;
                            cursor.setX(cursorPosX);
                        }
                    }
                } else if (code == KeyCode.UP) {
                    // move the cursor up one line if possible
                    if (cursorPosY > 0 && inputText.size() > 0) {
                        if (debug) {
                            System.out.println("Moving cursor up one space");
                        }
                        // there are lines above the cursor to move up
                        double wordHeight = inputText.get(cursorIndex - 1).getLayoutBounds().getHeight();
                        int index = getUpOneLine(cursorPosX, cursorPosY - wordHeight);
                        if (debug) {
                            System.out.println("Moving cursor to: " + inputText.get(index));
                        }
                        cursorIndex = index + 1;
                        cursorPosX = inputText.get(index).getX() + inputText.get(index).getLayoutBounds().getWidth();
                        cursorPosY = inputText.get(index).getY();
                        cursor.setX(cursorPosX);
                        cursor.setY(cursorPosY);
                    }
                } else if (code == KeyCode.DOWN) {
                    if (cursorPosY < inputText.size() && inputText.size() > 0) {
                        if (debug) {
                            System.out.println("Moving cursor down one space");
                        }
                        // there are lines above the cursor to move up
                        double wordHeight = inputText.get(cursorIndex - 1).getLayoutBounds().getHeight();
                        int index = getDownOneLine(cursorPosX, cursorPosY - wordHeight);
                        if (debug) {
                            System.out.println("Moving cursor to: " + inputText.get(index));
                        }
                        cursorIndex = index + 1;
                        cursorPosX = inputText.get(index).getX() + inputText.get(index).getLayoutBounds().getWidth();
                        cursorPosY = inputText.get(index).getY();
                        cursor.setX(cursorPosX);
                        cursor.setY(cursorPosY);
                    }
                }
            }
        }
    }

    private void printRoot() {
        //prints the current state of the root, for debugging purposes.
        System.out.println("root size is " + root.getChildren().size());
        System.out.println("PRINTING CONTENTS OF ROOT.");
        Object[] whatever = root.getChildren().toArray();
        for (int i = 1; i < whatever.length; i++) {
            Object hello = (Text) whatever[i];
            Text helloText = (Text) hello;
            System.out.print("'" + ((Text) hello).getText() + "', ");
        }
        System.out.println();
    }

    private void printLinkedList() {
        //prints the current state of the linked list, for debugging purposes.
        if (inputText.size() > 0) {
            System.out.println("X,Y position of first item in list: (" + inputText.get(0).getX() + ", " + inputText.get(0).getY() + ")");
            System.out.println("----PRINTING LINKED LIST-----");
            for (int i = 0; i < inputText.size(); i++) {
                System.out.print("'" + inputText.get(i).getText() + "', ");
            }
            System.out.println();
        }
    }

    private int getNearestText(double x, double y) {
        // given a position x,y, returns index of the item in the linked list that is closest to the click.
        //iterate through all elements in the list and choose the closest.
        double closestDistanceX = 1000;
        double closestDistanceY = 1000;
        int closestIndex = 0;
        // find the line number of the mouse click, if any.
        double smallestyDist = 1000;
        for (int i = 0; i < inputText.size(); i++) {
            // find the proper line number.
            double heightVal = (((inputText.get(i).getY() + inputText.get(i).getLayoutBounds().getHeight()) + inputText.get(i).getY()) / 2);
            double distanceY = Math.abs(y - heightVal);
            if (distanceY < smallestyDist) {
                smallestyDist = distanceY;
            }
        }
        for (int i = 0; i < inputText.size(); i++) {
            double widthVal = (((inputText.get(i).getX() + inputText.get(i).getLayoutBounds().getWidth()) + inputText.get(i).getX()) / 2);
            double heightVal = (((inputText.get(i).getY() + inputText.get(i).getLayoutBounds().getHeight()) + inputText.get(i).getY()) / 2);
            double distanceX = Math.abs(x - widthVal);
            double distanceY = Math.abs(y - heightVal);
            if (debug) {
                System.out.println("Smallest y distance is " + smallestyDist);
            }
            if (distanceX < closestDistanceX && distanceY == smallestyDist) {
                //the closest item so far has been found.
                closestIndex = i;
                closestDistanceX = distanceX;
                closestDistanceY = distanceY;
            }
        }
        return closestIndex;
    }

    private int getUpOneLine(double x, double y) {
        //given a position x,y, returns the index of the item in the list that is one line up.
        if (debug) {
            System.out.println("Getting index of value one line up from (" + x + ", " + y + ")");
        }
        double closestDistanceX = 1000;
        int closestIndex = 0;
        double charHeight = inputText.get(0).getLayoutBounds().getHeight();
        // go back a few lines till you find the y value of the preceding line.
        boolean foundPrevLine = false;
        int index = cursorIndex - 2;
        double yVal = inputText.get(index).getY();
        while (!foundPrevLine) {
            double yal = inputText.get(index).getY();
            if (yal < yVal) {
                if (debug) {
                    System.out.println("VALUE OF Y IS " + yal);
                }
                foundPrevLine = true;
                yVal = yal;
            }
            index -= 1;
        }
        for (int i = cursorIndex - 2; i > 0; i--) {
            //iterates back through the linked list, starting at cursor position, looking for the
            if (!inputText.get(i).getText().equals("\n")) {
                double distanceX = Math.abs(x - (inputText.get(i).getX() + inputText.get(i).getLayoutBounds().getWidth()));
                double yPos = inputText.get(i).getY();
                if (debug) {
                    System.out.println("++++++++ Distance to " + inputText.get(i).getText() + " is " + distanceX + "++++++++");
                    System.out.println("Closest Distance X is " + closestDistanceX);
                    System.out.println("Y value of " + inputText.get(i).getText() + " is " + yPos);
                    System.out.println("Yposition " + yPos + " == " + (y - (charHeight)) + " ?");
                }
                if (distanceX < closestDistanceX && yPos == yVal) {
                    // found closest spot so far.
                    closestIndex = i;
                    closestDistanceX = distanceX;
                }
            }
        }
        if (debug) {
            System.out.println("Found " + inputText.get(closestIndex).getText());
        }
        return closestIndex;
    }

    private int getDownOneLine(double x, double y) {
        //given a position x,y, returns the index of the item in the list that is one line down.
        if (debug) {
            System.out.println("Getting index of value one line down from (" + x + ", " + y + ")");
        }
        double closestDistanceX = 1000;
        int closestIndex = 0;
        for (int i = cursorIndex; i < inputText.size(); i++) {
            //iterates up through the linked list, starting at cursor position
            double distanceX = Math.abs(x - (inputText.get(i).getX() + inputText.get(i).getLayoutBounds().getWidth()));
            double yPos = inputText.get(i).getY();
            double charHeight = inputText.get(i).getLayoutBounds().getHeight();
            if (debug) {
                System.out.println("++++++++ Distance to " + inputText.get(i).getText() + " is " + distanceX + "++++++++");
                System.out.println("Closest Distance X is " + closestDistanceX);
                System.out.println("Y value of " + inputText.get(i).getText() + " is " + yPos);
                System.out.println("Yposition " + yPos + " < " + (y + (charHeight * 2)) + " ?");
            }
            if (distanceX < closestDistanceX && yPos > y && yPos == y + (charHeight * 2)) {
                // found closest spot so far.
                closestIndex = i;
                closestDistanceX = distanceX;
            }
        }
        if (debug) {
            System.out.println("Found " + inputText.get(closestIndex).getText());
        }
        return closestIndex;
    }

    private void resizeText() {
        //when font size is changed, iterates through the linked list and updates the position of all the elements.
        // NOTE: ALSO RESIZES & REPOSITIONS CURSOR GIVEN POSITION IN LINKED LIST.
        if (inputText.size() > 0) {
            cursor.setHeight(fontSize);
            if (debug) {
                System.out.println("RESIZING TEXT.");
            }
            for (int i = 0; i < inputText.size(); i++) {
                //resize all the entries in LL
                Text curr = inputText.get(i);
                curr.setFont(Font.font(fontName, fontSize));
            }
            double pw = pastWidths;
            double ph = pastHeights;
            int cI = cursorIndex;
            // now, iterate through all the entries in linked list and adjust their height/width.
            //temporarily reset the height/width/etc. values so we can call update method.
            pastWidths = 5;
            pastHeights = 0;
            cursorIndex = 0;
            updateText();
            // now, reset the values again.
            cursorIndex = cI;
            cursorPosX = inputText.get(cursorIndex - 1).getX() + inputText.get(cursorIndex - 1).getLayoutBounds().getWidth();
            cursorPosY = inputText.get(cursorIndex - 1).getY();
            pastWidths = cursorPosX;
            pastHeights = cursorPosY;
            cursor.setX(cursorPosX);
            cursor.setY(cursorPosY);
        }
    }

    private void updateText() {
        // re-renders the text in the text box, INCLUDING the cursor position.
        if (debug) {
            System.out.println("--------Updating Text--------");
            System.out.println("pastWidths is " + pastWidths);
            System.out.println("pastHeights is " + pastHeights);
        }
        double pastWidthsCopy = pastWidths;
        double pastHeightsCopy = pastHeights;
        for (int i = cursorIndex; i < inputText.size(); i++) {
            Text curr = inputText.get(i);
            String currText = curr.getText();
            if (debug) {
                System.out.println("currently updating inputText(" + i + "), which is " + currText);
                System.out.print("Changing " + currText + "'s position from " + curr.getX());
            }
            if ((currText.equals("\n")) || currText.equals("\r\n")) {
                // newline found
                pastWidthsCopy = marginLeft;
                pastHeightsCopy += curr.getLayoutBounds().getHeight();
                if (debug) {
                }
            } else {
                curr.setX(pastWidthsCopy);
                System.out.println(" to " + pastWidthsCopy);
                curr.setY(pastHeightsCopy);
                // now we need to reset the pastwidthscopy and pastheightscopy.
                if (curr.getX() >= scene.getWidth() - curr.getLayoutBounds().getWidth() - marginLeft) {
                    // if curr is at the end of a line, send following text to next line (IF A SPACE) ...
                    // by resetting pastwidthscopy to margin and incrementing pastheightscopy by height value.
                    Boolean foundSpace = false;
                    // now, iterate through all the previous entries to find a space, and wrap text on space (IF POSSIBLE!)
                    Text cur2 = inputText.get(i);
                    int ind = i;
                    while (!foundSpace && ind >= 0) {
                        //iterate through all previous entries in list to find a space. if no space found, wrap text at end of line.
                        if (debug) {
                            System.out.println("searching for space to perform word wrap.");
                        }
                        if (cur2.getText().equals(" ")) {
                            // space is found, wrap text here.
                            pastWidthsCopy = marginLeft;
                            pastHeightsCopy += curr.getLayoutBounds().getHeight();
                            foundSpace = true;
                            i = ind;
                        }
                        ind -= 1;
                        cur2 = inputText.get(ind);
                    }
                    if (!foundSpace) {
                        pastWidthsCopy = marginLeft;
                        pastHeightsCopy += curr.getLayoutBounds().getHeight();
                    }
                } else {
                    // if curr is not at the end of a line, just increment pastWidthsCopy.
                    pastWidthsCopy += curr.getLayoutBounds().getWidth();
                }
            }
        }
    }

    /**
     * An EventHandler to handle changing the color of the cursor.
     */
    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors = {Color.WHITE, Color.BLACK};

        RectangleBlinkEventHandler() {
            // Set the color to be the first color in the list.
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }

        /**
         * Makes the cursor change color periodically.
         */
        public void makeRectangleColorChange() {
            // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
            // every 1 second.
            final Timeline timeline = new Timeline();
            // The rectangle should continue blinking forever.
            timeline.setCycleCount(Timeline.INDEFINITE);
            RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        }
    } // end class RectangleBlinkEventHandler

    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        // The Scene represents the window: its height and width will be the height and width
        // of the window displayed.
        cursor.setHeight(fontSize);
        cursor.setWidth(1);
        int windowWidth = 500;
        int windowHeight = 500;
        scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);
        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        // Register the event handler to be called for all KEY_PRESSED and KEY_TYPED events.
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(new MouseClickEventHandler(root));
        primaryStage.setTitle("Editable Text Display");
        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
        if (readFileMode) {
            readFile(); // <-- READ THE FILE YO
        }
        makeCursorColorChange();

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                int newWidth = getDimensionInsideMargin(newScreenWidth.intValue());
//                scene.setFitWidth(newAllenWidth);
               int CIC = cursorIndex;
                double pwC = pastWidths;
                double pHC = pastHeights;
                cursorIndex = 0;
                pastWidths = marginLeft;
                pastHeights = 0;
                updateText();
                cursorIndex = CIC;
                pastWidths = pwC;
                pastHeights = pHC;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenHeight,
                    Number newScreenHeight) {
                int newHeight = getDimensionInsideMargin(newScreenHeight.intValue());
//                allenView.setFitHeight(newAllenHeight);
                int CIC = cursorIndex;
                double pwC = pastWidths;
                double pHC = pastHeights;
                cursorIndex = 0;
                pastWidths = marginLeft;
                pastHeights = 0;
                updateText();
                cursorIndex = CIC;
                pastWidths = pwC;
                pastHeights = pHC;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            }
        });

    }
    private int getDimensionInsideMargin(int outsideDimension) {
        return outsideDimension - 2 * (int) marginLeft;
    }

    public void makeCursorColorChange() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void undoFunction() {
        if (changeLog.size() > 0) {
            ChangeSaver cs = changeLog.pop();
            if (cs.action.equals("ADD")) {
                // this means DELETE the item added
                int ind = cs.index;
                undoLog.add(new ChangeSaver("DELETE", cursorPosX, cursorPosY, ind, cs.text));
                if (inputText.size() == ind) {
                    inputText.removeLast();
                } else {
                    inputText.remove(ind);
                }
                root.getChildren().remove(ind);
                cursorIndex = ind + 1;
                pastWidths = cs.cursorX;
                pastHeights = cs.cursorY;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            } else if (cs.action.equals("DELETE")) {
                // this means ADD the item deleted back
                int ind = cs.index;
                undoLog.add(new ChangeSaver("ADD", cs.cursorX, cs.cursorY, ind, cs.text));
                if (inputText.size() == ind) {
                    inputText.add(cs.text);
                    root.getChildren().add(cs.text);
                } else {
                    inputText.add(ind, cs.text);
                    root.getChildren().add(ind, cs.text);
                }
                cursorIndex = ind + 1;
                pastWidths = cs.cursorX;
                pastHeights = cs.cursorY;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            }
        }
    }

    private void redoFunction() {
        if (undoLog.size() > 0) {
            ChangeSaver cs = undoLog.pop();
            if (cs.action.equals("ADD")) {
                // this means DELETE the item added
                int ind = cs.index;
                changeLog.add(new ChangeSaver("DELETE", cs.cursorX, cs.cursorY, ind, cs.text));
                if (inputText.size() == ind) {
                    inputText.removeLast();
                } else {
                    inputText.remove(ind);
                }
                root.getChildren().remove(ind + 1);
                cursorIndex = ind + 1;
                pastWidths = cs.cursorX;
                pastHeights = cs.cursorY;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            } else if (cs.action.equals("DELETE")) {
                // this means ADD the item deleted back
                int ind = cs.index;
                changeLog.add(new ChangeSaver("ADD", cs.cursorX, cs.cursorY, ind, cs.text));
                if (ind + 1 >= inputText.size()) {
                    inputText.add(cs.text);
                    root.getChildren().add(cs.text);
                } else {
                    inputText.add(ind, cs.text);
                    root.getChildren().add(ind, cs.text);
                }
                cursorIndex = ind + 1;
                pastWidths = cs.cursorX;
                pastHeights = cs.cursorY;
                cursorPosX = pastWidths;
                cursorPosY = pastHeights;
                cursor.setX(cursorPosX);
                cursor.setY(cursorPosY);
            }
        }
    }

    private static void saveFile() {
        //saves all the items in the file to the given file name.
        if (!readFileMode) {
            // do nothing, this means there is no file input.

        }
        String outputFilename = inputFilename;
        try {
            File inputFile = new File(inputFilename);
            // Check to make sure that the file exists:
            if (!inputFile.exists()) {
                System.out.println("Unable to save because file with name " + inputFilename
                        + " does not exist");
                return;
            }
            // Create a FileWriter to write to outputFilename. FileWriter will overwrite any data
            // already in outputFilename.
            FileWriter writer = new FileWriter(outputFilename);
            // read everything from linked list and write to the file.
            for (int i = 0; i < inputText.size(); i++) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                String charRead = (String) inputText.get(i).getText();
                writer.write(charRead);
                if (debug) {
                    System.out.println("Writing " + charRead + " to file.");
                }
            }
            if (debug) {
                System.out.println("Successfully saved data to "
                        + outputFilename);
            }
            // Close the writer.
            writer.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }


    private static void readFile() {
       /* Reads the file given by string args in main and outputs it to the text box.
        Opening a file should take time proportional to the length of the file,
        not to the length of the file squared. */
        FileReader reader;
        BufferedReader bufferedReader;
        try {
            File inputFile = new File(inputFilename);
            // Check to make sure that the input file exists!
            if (!inputFile.exists()) {
                System.out.println("Unable to open file because " + inputFilename
                        + " does not exist");
                return;
            }
            reader = new FileReader(inputFile);
            bufferedReader = new BufferedReader(reader);
            // Create a FileWriter to write to outputFilename. FileWriter will overwrite any data
            // already in inputFilename.
            // writer = new FileWriter(inputFilename);
            int intRead = 0;
            // Keep reading from the file input read() returns -1, which means the end of the file
            // was reached.
            if (debug) {
                System.out.println();
                System.out.println("========== Reading file:===========");
            }
            while ((intRead = bufferedReader.read()) != -1) {
                // The integer read can be cast to a char, because we're assuming ASCII.
                Character charRead = (char) intRead;
                String stringRead = charRead.toString();
                if (debug) {
                    System.out.print(charRead);
                }
                Text newText = new Text(pastWidths, pastHeights, stringRead);
                newText.setTextOrigin(VPos.TOP);
                newText.setFont(Font.font(fontName, fontSize));
                inputText.add(newText);
                root.getChildren().add(newText);
                cursorIndex += 1;
                if (cursorPosX < scene.getWidth() - marginLeft) {
                    // if not at end of line, increment cursorposx but not cursorposy.
                    pastWidths += newText.getLayoutBounds().getWidth();
                    cursorPosX += newText.getLayoutBounds().getWidth();
                    cursor.setX(cursorPosX);
                } else {
                    // if at end of line, reset cursorposx and increment cursorposy.
                    pastWidths = marginLeft;
                    cursorPosX = marginLeft;
                    cursor.setX(cursorPosX);
                    pastHeights += newText.getLayoutBounds().getHeight();
                    cursorPosY += newText.getLayoutBounds().getHeight();
                    cursor.setY(cursorPosY);
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (debug) {
            System.out.println();
            System.out.println("======= File read finished. =======");
        }
//        displayTextInBox();
    }

    /**
     * An event handler that displays the current position of the mouse whenever it is clicked.
     */
    private class MouseClickEventHandler implements EventHandler<MouseEvent> {
        /**
         * A Text object that will be used to print the current mouse position.
         */
        Text positionText;
        MouseClickEventHandler(Group root) {
            // For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the text)
            positionText.setTextOrigin(VPos.BOTTOM);
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            if (debug) {
                printRoot();
                System.out.println("MOUSE CLICKED!");
            }
            // Because we registered this EventHandler using setOnMouseClicked, it will only be called
            // with mouse events of type MouseEvent.MOUSE_CLICKED.  A mouse clicked event is
            // generated anytime the mouse is pressed and released on the same JavaFX node.
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            int closestInd = getNearestText(mousePressedX, mousePressedY);
            if (debug) {
                System.out.println("The closest character to " +
                        "(" + mousePressedX + ", " + mousePressedY + ") is " + inputText.get(closestInd).getText());
            }
            cursorIndex = closestInd + 1;
            cursorPosX = inputText.get(closestInd).getX() + inputText.get(closestInd).getLayoutBounds().getWidth();
            cursorPosY = inputText.get(closestInd).getY();
            cursor.setX(cursorPosX);
            cursor.setY(cursorPosY);
            pastWidths = cursorPosX;
            pastHeights = cursorPosY;
        }
    } // end class mouseClickEventHandler

    public static void main(String[] args) {
        /* the first command line argument passed to Editor must be the name of a file to edit,
            and this argument is required. If that file already exists, Editor should open it and display
            its contents (with the starting cursor position at the beginning of the file); otherwise,
            Editor should begin with an empty file.
         */
        Integer argsLength = args.length;
        if (argsLength > 0) {
            System.out.println("args[0] is " + args[0]);
            if (args[0].equals("Debug")) {
                // if the first argument is debug
                debug = true;
                System.out.println("DEBUG MODE INITIALIZED.");
            } else {
                // the first argument is not debug, which means it must be a file name
                readFileMode = true;
                inputFilename = args[0];
                if (argsLength > 1) {
                    System.out.println("args[1] is " + args[1]);
                    // if there are 2 arguments
                    if (args[1].equals("Debug")) {
                        debug = true;
                        System.out.println("DEBUG MODE INITIALIZED.");
                    }
                }
            }
        }
        else{
            throw new RuntimeException("Expected usage: Editor <filename>");
        }
        launch(args);
    }
}// end class Editor