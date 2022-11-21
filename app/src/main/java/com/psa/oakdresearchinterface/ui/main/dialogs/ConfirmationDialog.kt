package com.psa.oakdresearchinterface.ui.main.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import com.psa.oakdresearchinterface.databinding.DialogConfirmationBinding


class ConfirmationDialog(context: Context, @StringRes var cancelString: Int, @StringRes var continueString: Int, var callback: () -> Unit): Dialog(context) {
    private lateinit var binding: DialogConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmationBinding.inflate(layoutInflater)

        binding.cancelButton.setText(cancelString)
        binding.continueButton.setText(continueString)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.continueButton.setOnClickListener {
            dismiss()
            callback()
        }

        setContentView(binding.root)
    }
}