package com.yuexiaohome.tempalarm.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yuexiaohome.tempalarm.R;

public class InputMinuteFragment extends BlurDialogFragment implements TextView.OnEditorActionListener
{
    @Bind(R.id.edit_input_minute)
    EditText edit_input_minute;

    @Override
    public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
    {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            setAlarm(v);
            return true;
        }
        return false;
    }

    public interface InputMinuteFragmentListener
    {
        void onFinishInputMinute(String inputText);
    }

    @Override
    protected View getContainerForDismiss()
    {
        return getView().findViewById(R.id.dialog_content_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
            Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_input_minute,container,false);
        ButterKnife.bind(this,view);


//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("请输入分钟数");
        edit_input_minute.setOnEditorActionListener(this);


        return view;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.button_cancel})
    public void closeDialog(View view)
    {
        dismiss();
    }

    @OnClick(R.id.button_set_alarm)
    public void setAlarm(View view)
    {
        InputMinuteFragmentListener activity=(InputMinuteFragmentListener)getActivity();
        Editable text=edit_input_minute.getText();
        if (edit_input_minute.getText().length() == 0) {
            edit_input_minute.setError("请输入分钟数。");
            return;
        }
        else
        {
            activity.onFinishInputMinute(text.toString());
            this.dismiss();
        }

    }

}

