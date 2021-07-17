package com.rsschool.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var chosenOption = 0

    private var listener: QuizListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionNumber = arguments?.getInt(QUESTION_NUMBER_KEY) ?: 0
        val checkedOption = arguments?.getInt(CHECKED_NUMBER_KEY)
        val dataQuestion = arguments?.getString(DATA_QUESTION_KEY)
        val question = dataQuestion?.substringBefore('#')
        val answers = getAnswers(dataQuestion)

        binding.question.text = question

        binding.optionOne.text = answers[1]
        binding.optionTwo.text = answers[2]
        binding.optionThree.text = answers[3]
        binding.optionFour.text = answers[4]
        binding.optionFive.text = answers[5]

        when (checkedOption) {
            0 -> binding.nextButton.isEnabled = false
            1 -> binding.optionOne.isChecked = true
            2 -> binding.optionTwo.isChecked = true
            3 -> binding.optionThree.isChecked = true
            4 -> binding.optionFour.isChecked = true
            5 -> binding.optionFive.isChecked = true
        }

        chosenOption = checkedOption ?: 0

        if (questionNumber == LAST_QUESTION_NUMBER) {
            binding.nextButton.text = "Submit"
        }

        if (questionNumber == FIRST_QUESTION_NUMBER) {
            binding.previousButton.isEnabled = false
            binding.toolbar.navigationIcon = null
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.nextButton.isEnabled = true
            chosenOption = when (checkedId) {
                R.id.option_one -> 1
                R.id.option_two -> 2
                R.id.option_three -> 3
                R.id.option_four -> 4
                R.id.option_five -> 5
                else -> 0
            }

        }

        listener = activity as? QuizListener

        binding.nextButton.setOnClickListener {
            listener?.next(chosenOption, questionNumber)
        }

        binding.previousButton.setOnClickListener {
            listener?.previous(chosenOption, questionNumber)
        }

        binding.toolbar.title = "Question ${questionNumber + 1}"
        binding.toolbar.setNavigationOnClickListener {
            listener?.previous(chosenOption, questionNumber)
        }
    }

    private fun getAnswers(dataQuestion: String?): List<String> {
        dataQuestion ?: return emptyList()
        return dataQuestion.split(DELIMITER)
    }

    interface QuizListener {

        fun next(chosenOption: Int, questionNumber: Int)

        fun previous(chosenOption: Int, questionNumber: Int)
    }

    companion object {

        private const val DATA_QUESTION_KEY = "TEXT_Q"
        private const val QUESTION_NUMBER_KEY = "QUESTION_NUMBER"
        private const val CHECKED_NUMBER_KEY = "CHECKED_NUMBER"
        private const val DELIMITER = '#'
        private const val FIRST_QUESTION_NUMBER = 0
        private const val LAST_QUESTION_NUMBER = 4

        fun newInstance(
            text: String,
            chekedOptionNumber: Int,
            questionNumber: Int
        ): QuizFragment {
            return QuizFragment().apply {
                arguments = bundleOf(
                    DATA_QUESTION_KEY to text,
                    CHECKED_NUMBER_KEY to chekedOptionNumber,
                    QUESTION_NUMBER_KEY to questionNumber
                )
            }
        }
    }
}