package us.detr.denojetlinter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import us.detr.denojetlinter.DenoLinter.lintFile

class DenoAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiFile) return

        // Parse the output
        val lintingResult = lintFile(element)

        // Generate annotations
        annotateDiagnostics(holder, lintingResult.diagnostics, element)
    }

    private fun annotateDiagnostics(holder: AnnotationHolder, diagnostics: List<Diagnostic>, file: PsiFile) {
        for (diagnostic in diagnostics) {
            val document = file.viewProvider.document ?: continue
            val startLineOffset = document.getLineStartOffset(diagnostic.range.start.line - 1)
            val endLineOffset = document.getLineStartOffset(diagnostic.range.end.line - 1)

            val startOffset = startLineOffset + diagnostic.range.start.col
            val endOffset = endLineOffset + diagnostic.range.end.col

            val message =
                "${diagnostic.message}. ${diagnostic.hint} (${diagnostic.code})"

            holder.newAnnotation(HighlightSeverity.ERROR, message)
                .range(TextRange(startOffset, endOffset))
                .create()
        }
    }
}
