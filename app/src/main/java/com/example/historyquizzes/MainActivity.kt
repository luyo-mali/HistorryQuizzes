package com.example.historyquizzes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    HistoryQuizzesApp()
                }
            }
        }
    }


    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("ContextCastToActivity")
    @Composable
    fun HistoryQuizzesApp() {
        var screen by remember { mutableStateOf("welcome") }
        val questions = listOf(
            "The Berlin Wall fell in 1989",
            "Nelson Mandela was the president in 1994",
            "World War I ended in 1920",
            "The Roman Empire collapsed in 476 AD",
            "The American civil war ended in 1865"
        )
        val answers = listOf(true, false, true, true, true)
        var currentIndex by remember { mutableIntStateOf(0) }
        var score by remember { mutableIntStateOf(0) }
        val userAnswers = remember { mutableListOf<Boolean>() }
        val activity = LocalContext.current as? Activity


        when (screen) {
            "welcome" -> WelcomeScreen { screen = "quiz" }
            "quiz" -> QuizScreen(
                question = questions [currentIndex],
                onAnswer = { userAnswers ->
                    val correct = answers[currentIndex] == userAnswers
                    if (correct) score++
                    userAnswers.and(userAnswers)
                },
                onNext = {
                    if (currentIndex < questions.lastIndex) {
                        currentIndex++
                    } else {
                        screen = "score"
                    }
                },
                correctAnswer = answers[currentIndex]
            )

            "score" -> ScoreScreen(
                score = score,
                total = questions.size,
                onReview = { screen = "review" },
                onExit = { activity?.finish()
                }
            )

            "review" -> ReviewScreen(
                questions = questions,
                answers = answers,
                userAnswers = userAnswers
            )
        }
    }
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to History Flashcards!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Test your knowledge with five True/False questions.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onStart) {
            Text("Start")
        }
    }
}
@Composable
fun QuizScreen(
             question: String,
             onAnswer: (Boolean) -> Unit,
             onNext: () -> Unit,
             correctAnswer: Boolean
) {
    // This ensures selected resets every time 'question' changes
    var selected by remember(question) { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(question, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                selected = true
                onAnswer(true)
            }, enabled = selected == null) {
                Text("True")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                selected = false
                onAnswer(false)
            }, enabled = selected == null) {
                Text("False")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        selected?.let {
            val feedback = if (it == correctAnswer) "Correct!" else "Incorrect"
            Text(feedback, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ScoreScreen(score: Int, total: Int, onReview: () -> Unit, onExit: () -> Unit) {
    val feedback = if (score >= 3) "Great job!" else "Keep practising!"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Your Score: $score / $total", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(feedback, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onReview) {
            Text("Review")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onExit) {
            Text("Exit")
        }
    }
}

@Composable
fun ReviewScreen(questions: List<String>, answers: List<Boolean>, userAnswers: List<Boolean?>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Review Answers", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        questions.forEachIndexed { index, question ->
            val correct = answers[index]
            val user = userAnswers.getOrNull(index)
            Text(question)
            Text("Correct Answer: $correct | Your Answer: $user")
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}