package com.phonedeals.ascom.phonestrore.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.interfaces.OnAcceptUseAgreementListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.util.AppUtils;



public class UseAgreementDialog extends Dialog {


    private final OnAcceptUseAgreementListener onAcceptUseAgreementListener;

    private final String useAgreement;

    public UseAgreementDialog(@NonNull Context context, OnAcceptUseAgreementListener onAcceptUseAgreementListener, String useAgreement) {
        super(context);
        this.onAcceptUseAgreementListener = onAcceptUseAgreementListener;
        this.useAgreement = useAgreement;
    }

    public void showDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_use_agreement, null);
        TextView agreementTextView = dialogView.findViewById(R.id.use_agreement_textView);
        AppUtils.applyMediumFont(agreementTextView);
        agreementTextView.setScroller(new Scroller(dialogView.getContext()));
        agreementTextView.setVerticalScrollBarEnabled(true);
        agreementTextView.setMovementMethod(new ScrollingMovementMethod());
        agreementTextView.setText(useAgreement);
        Button acceptAgreement = dialogView.findViewById(R.id.accept_agreement_button);
        acceptAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onAcceptUseAgreementListener.acceptUseAgreement();
            }
        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
        show();
    }
}
