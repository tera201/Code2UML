package org.tera201.code2uml

import org.tera201.code2uml.java20.console.JavaParserRunnerDB
import org.tera201.code2uml.util.messages.DataBaseUtil
import javax.swing.JProgressBar
import javax.swing.JTextArea

class AnalyzerBuilder(
    private val language: Language,
    private val projectName: String,
    private val modelName: String,
    private val path: String,
    private var pathToDB: String
) {
    private var numThread: Int = 0
    private var textArea: JTextArea? = null
    private var progressBar: JProgressBar? = null

    fun threads(numThread: Int?) = apply { this.numThread = numThread ?: 0 }

    fun textArea(textArea: JTextArea?) = apply { this.textArea = textArea }

    fun progressBar(progressBar: JProgressBar) = apply { this.progressBar = progressBar }

    fun setPathToDB(pathToDB: String) = apply { this.pathToDB = pathToDB }

    fun buildDB(dataBaseUtil: DataBaseUtil): Int = when (language) {
        Language.Java -> {
            val javaParserRunner = JavaParserRunnerDB()
            val javaFiles = javaParserRunner.collectFiles(path)
            javaParserRunner.buildModel(dataBaseUtil, projectName, modelName, javaFiles, textArea, progressBar, numThread)
        }
        Language.Cpp -> TODO()
    }
}
