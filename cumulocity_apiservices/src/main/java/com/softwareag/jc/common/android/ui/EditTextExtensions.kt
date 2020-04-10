package com.softwareag.jc.common.android.ui

import android.widget.EditText

class EditTextExtensions {

    companion object {

        fun EditText.setReadOnly(readOnly: Boolean) {

            this.setFocusable(!readOnly);
            this.setFocusableInTouchMode(!readOnly);
            this.setClickable(!readOnly);
            this.setLongClickable(!readOnly);
            this.setCursorVisible(!readOnly);
        }
    }
}