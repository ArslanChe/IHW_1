data class Movie(
    var title: String,
    var genre: String,
    var director: String,
    var releaseYear: Int,
    var durationMinutes: Int,
    /* другие свойства фильма */
) {
    companion object {
        private val movies = mutableListOf<Movie>()

        // Метод для поиска фильма по названию
        private fun findMovieByTitle(title: String): Movie? {
            return movies.find { it.title == title }
        }
    }

    fun addMovie(): Boolean {
        if (findMovieByTitle(title) == null) {
            movies.add(this)
            return true
        }
        return false
    }

    fun removeMovie(): Boolean {
        return true
    }

    // Другие методы для работы с данными фильма
}
