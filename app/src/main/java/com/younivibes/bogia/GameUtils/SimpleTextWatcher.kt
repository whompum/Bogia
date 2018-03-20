package com.younivibes.bogia.GameUtils

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by bryan on 3/17/2018.
 */
open class SimpleTextWatcher: TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}