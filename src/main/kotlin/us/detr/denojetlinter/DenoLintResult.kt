package us.detr.denojetlinter

import kotlinx.serialization.Serializable

@Serializable
data class LintingResult(
    val diagnostics: List<Diagnostic>,
    val errors: List<Error>
)

@Serializable
data class Diagnostic(
    val range: Range,
    val filename: String,
    val message: String,
    val code: String,
    val hint: String
)

@Serializable
data class Range(
    val start: Position,
    val end: Position
)

@Serializable
data class Position(
    val line: Int,
    val col: Int,
    val bytePos: Int
)

@Serializable
data class Error(
    val file_path: String,
    val message: String
)
