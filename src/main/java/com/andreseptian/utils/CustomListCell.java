package com.andreseptian.utils;

import com.andreseptian.entities.SocialMedia;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;

public class CustomListCell extends ListCell<SocialMedia> {

    @Override
    protected void updateItem(SocialMedia item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setCursor(Cursor.DEFAULT);
        } else {
            setText(item.name());
            setCursor(Cursor.HAND);
        }
    }

}
