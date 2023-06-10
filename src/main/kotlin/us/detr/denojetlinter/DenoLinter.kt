package us.detr.denojetlinter

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object DenoLinter {
    private var json = Json { ignoreUnknownKeys = true }

    fun lintFile(file: PsiFile): LintingResult {

        if (!DenoChecker.isDenoAvailable(file.project)) {
            return LintingResult(emptyList(), emptyList())
        }

        val document: Document = file.viewProvider.document
            ?: return LintingResult(emptyList(), emptyList())


        // The command to execute
        val command = listOf("deno", "lint", "--json", "-")

        val projectDirPath = file.project.basePath
        val projectDir = if (projectDirPath != null) File(projectDirPath) else null

        // Execute the command
        val process = ProcessBuilder(command)
            .apply { if (projectDir != null) directory(projectDir) }
            .start()

        val outputStream = process.outputStream
        outputStream.write(document.text.toByteArray())
        outputStream.close()

        val executorService = Executors.newFixedThreadPool(1)

        val outputFuture = executorService.submit(Callable {
            process.inputStream.bufferedReader().use { it.readText() }
        })

        executorService.shutdown()

        process.waitFor()

        val output = outputFuture.get()

        return json.decodeFromString(output)
    }
}