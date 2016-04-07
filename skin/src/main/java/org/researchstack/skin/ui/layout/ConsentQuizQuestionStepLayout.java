package org.researchstack.skin.ui.layout;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.skin.R;
import org.researchstack.skin.step.ConsentQuizQuestionStep;

public class ConsentQuizQuestionStepLayout extends RelativeLayout implements StepLayout
{
    private ConsentQuizQuestionStep step;
    private StepResult<Boolean>     result;
    private StepCallbacks           callbacks;

    private TextView    resultSummary;
    private TextView    resultTitle;
    private RadioGroup  radioGroup;
    private SubmitBar   submitBar;
    private RadioButton radioFalse;
    private RadioButton radioTrue;
    private View        radioItemBackground;

    public ConsentQuizQuestionStepLayout(Context context)
    {
        super(context);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentQuizQuestionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentQuizQuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    public void initializeStep()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.item_quiz_question, this, true);

        ((TextView) findViewById(R.id.title)).setText(step.getTitle());

        radioGroup = (RadioGroup) findViewById(R.id.rdio_group);

        radioTrue = (RadioButton) findViewById(R.id.btn_true);
        radioTrue.setText(R.string.rss_btn_true);

        radioFalse = (RadioButton) findViewById(R.id.btn_false);
        radioFalse.setText(R.string.rss_btn_false);

        submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveAction(v -> onSubmit());
        submitBar.getNegativeActionView().setVisibility(GONE);

        resultTitle = (TextView) findViewById(R.id.quiz_result_title);
        resultSummary = (TextView) findViewById(R.id.quiz_result_summary);

        radioItemBackground = findViewById(R.id.quiz_result_item_background);
    }

    public void onSubmit()
    {
        if(isAnswerValid())
        {
            boolean answer = step.getQuestion().getExpectedAnswer().equals("true");
            boolean selected = radioGroup.getCheckedRadioButtonId() == R.id.btn_true;
            boolean answerCorrect = answer == selected;

            if(resultTitle.getVisibility() == View.GONE)
            {
                int resultTextColor = answerCorrect ? 0xFF67bd61 : 0xFFc96677;
                int radioBackground = Color.argb(51,
                        //20% alpha
                        Color.red(resultTextColor),
                        Color.green(resultTextColor),
                        Color.blue(resultTextColor));

                // Disable both buttons to prevent further action
                radioFalse.setEnabled(false);
                radioTrue.setEnabled(false);

                // Set the drawable of the current checked button, with correct color tint
                int resId = answerCorrect ? R.drawable.ic_check : R.drawable.ic_window_close;
                Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, resultTextColor);
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                checkedRadioButton.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        drawable,
                        null);

                // This is a work around for phones with api > 17 (but applies to all api versions).
                // The horizontal offset of a radioButton cannot be set but we need our RadioButtons
                // to have an offset of 48dp (to align w/ ToolBar title). Padding can be set but
                // that will only push the text, not button-drawable. To solve this issue, we have a
                // view that is floating behind out RadioGroup and position and height is set
                // dynamically
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) radioItemBackground
                        .getLayoutParams();
                params.addRule(ALIGN_TOP, radioTrue.isChecked() ? R.id.rdio_group : 0);
                params.addRule(ALIGN_BOTTOM, radioTrue.isChecked() ? 0 : R.id.rdio_group);
                params.height = checkedRadioButton.getHeight();
                radioItemBackground.setBackgroundColor(radioBackground);
                radioItemBackground.setVisibility(View.VISIBLE);

                //Set our Result-title
                String resultTitle = answerCorrect
                        ? getContext().getString(R.string.rss_quiz_evaluation_correct)
                        : getContext().getString(R.string.rss_quiz_evaluation_incorrect);

                this.resultTitle.setVisibility(View.VISIBLE);
                this.resultTitle.setText(resultTitle);
                this.resultTitle.setTextColor(resultTextColor);

                //Build and set our result-summary
                String explanation;

                if(answerCorrect)
                {
                    explanation = getContext().getString(R.string.rss_quiz_question_explanation_correct,
                            step.getQuestion().getPositiveFeedback());
                }
                else
                {
                    explanation = getContext().getString(R.string.rss_quiz_question_explanation_incorrect,
                            step.getQuestion().getExpectedAnswer(),
                            step.getQuestion().getNegativeFeedback());
                }


                resultSummary.setText(Html.fromHtml(explanation));
                resultSummary.setVisibility(View.VISIBLE);

                // Change the submit bar positive-title to "next"
                submitBar.setPositiveTitle(R.string.rsb_next);
            }
            else
            {
                // Save the result and go to the next question
                result.setResult(answerCorrect);
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
            }
        }

    }

    public boolean isAnswerValid()
    {
        if(radioGroup.getCheckedRadioButtonId() == - 1)
        {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

}
