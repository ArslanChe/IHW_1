class CinemaManager {
    private val fileHandler = FileHandler()
    private val users = mutableListOf<User>()
    private val movies = mutableListOf<Movie>()
    private val showtimes = mutableListOf<Showtime>()
    private val tickets = mutableListOf<Ticket>()

    init {
        loadAllData()
    }


    private fun registerUser(username: String, password: String): Boolean {
        if (users.find { it.username == username } == null) {
            val newUser = User(username, password)
            users.add(newUser)
            return true
        } else {
            return false
        }
    }

    private fun loginUser(username: String, password: String): Boolean {
        println(users)
        val existingUser = users.find { it.username == username }
        return existingUser?.authenticate(password) ?: false
    }


    private fun addMovie(
        title: String,
        genre: String,
        director: String,
        releaseYear: Int,
        durationMinutes: Int
    ): Boolean {
        val newMovie = Movie(title, genre, director, releaseYear, durationMinutes)
        if (newMovie.addMovie()) {
            movies.add(newMovie)
            saveDataToFiles()
            return true
        }
        return false
    }

    private fun removeMovie(existingMovie: Movie?): Boolean {
        if (existingMovie != null && existingMovie.removeMovie()) {
            movies.remove(existingMovie)
            saveDataToFiles()
            return true
        }
        return false
    }

    private fun editMovie(
        movie: Movie?,
        newTitle: String,
        newGenre: String,
        newDirector: String,
        newReleaseYear: Int,
        newDurationMinutes: Int
    ): Boolean {
        return if (movie != null) {
            movie.title = newTitle
            movie.genre = newGenre
            movie.director = newDirector
            movie.releaseYear = newReleaseYear
            movie.durationMinutes = newDurationMinutes
            true
        } else {
            false
        }
    }

    private fun sellTicket(showtime: Showtime, seatRow: Int, seatNumber: Int): Boolean {
        val seat = Seat(seatRow, seatNumber)
        if (showtime.reserveSeat(seatRow, seatNumber)) {
            val ticket = Ticket(showtime, seat, calculateTicketPrice(showtime))
            tickets.add(ticket)
            return true
        }
        return false
    }

    private fun returnTicket(showtime: Showtime, seatRow: Int, seatNumber: Int): Boolean {
        val ticket = tickets.find { it.showtime == showtime && it.seat.row == seatRow && it.seat.number == seatNumber }
        println(ticket)
        if (ticket != null) {
            // Возвращаем билет
            if (ticket.returnTicket()) {
                tickets.remove(ticket)
                return true
            }
        }
        return false
    }

    private fun editShowtime(showtime: Showtime?, startTime: String, newDurationMinutes: Int): Boolean {

        return if (showtime != null) {
            showtime.durationMinutes = newDurationMinutes
            showtime.startTime = startTime
            true
        } else {
            false
        }
    }


    private fun addShowtime(
        movie: Movie?,
        startTime: String,
        durationMinutes: Int,
        availableSeats: Array<Array<Boolean>>,
        hallSchema: Array<Array<Boolean>>
    ): Boolean {

        if (movie != null) {
            val newShowtime = Showtime(movie, startTime, durationMinutes, availableSeats, hallSchema)

            // Проверяем, не пересекаются ли времена существующих сеансов
            if (showtimes.none { it.startTime == startTime }) {
                if (newShowtime.addShowtime()) {
                    showtimes.add(newShowtime)
                    saveDataToFiles() // Сохраняем изменения в файлах
                    return true
                }
            }
        }
        return false
    }

    private fun removeShowtime(movieTitle: String, startTime: String): Boolean {
        val movie = movies.find { it.title == movieTitle }
        val showtime = showtimes.find { it.movie == movie && it.startTime == startTime }

        if (movie != null && showtime != null && showtime.removeShowtime()) {
            showtimes.remove(showtime)
            saveDataToFiles() // Сохраняем изменения в файлах
            return true
        }
        return false
    }

    private fun loadAllData() {
        loadUsers()
        loadMovies()
        loadShowtimes()
        loadTickets()
    }

    private fun saveDataToFiles() {
        fileHandler.saveDataToFile(users, "src/main/resources/users.json")
        fileHandler.saveDataToFile(movies, "src/main/resources/movies.json")
        fileHandler.saveDataToFile(showtimes, "src/main/resources/showtimes.json")
        fileHandler.saveDataToFile(tickets, "src/main/resources/tickets.json")
    }

    private fun loadUsers() {
        val usersData = fileHandler.loadUsersFromFile("src/main/resources/users.json")
        usersData?.let { users.addAll(it) }
    }

    private fun loadMovies() {
        val moviesData = fileHandler.loadMoviesFromFile("src/main/resources/movies.json")
        moviesData?.let { movies.addAll(it) }
    }

    private fun loadShowtimes() {
        val showtimesData = fileHandler.loadShowtimesFromFile("src/main/resources/showtimes.json")
        showtimesData?.let { showtimes.addAll(it) }
    }

    private fun loadTickets() {
        val ticketsData = fileHandler.loadTicketsFromFile("src/main/resources/tickets.json")
        ticketsData?.let { tickets.addAll(it) }
    }

    fun startCinema() {

        runCinemaMenu()
    }

    // Метод для расчета стоимости билета (может быть дополнен более сложной логикой)
    private fun calculateTicketPrice(showtime: Showtime): Double {

        return 10.0 * showtime.durationMinutes
    }

    private fun getAvailableShowtimes(movie: Movie?): List<Showtime> {
        return showtimes.filter { it.movie == movie }
    }

    private fun runCinemaMenu() {
        fun selectMovieFromList(): Movie? {
            println("Выберите фильм по номеру:")
            movies.forEachIndexed { index, movie ->
                println("${index + 1}. ${movie.title}")
            }

            val movieNumber = readlnOrNull()?.toIntOrNull()

            return if (movieNumber != null && movieNumber in 1..movies.size) {
                movies[movieNumber - 1]
            } else {
                println("Неверный выбор.")
                null
            }
        }

        fun displayShowtimes() {
            println("Список всех сеансов:")
            showtimes.forEachIndexed { index, showtime ->
                println("${index + 1}. Фильм: ${showtime.movie.title}, Время: ${showtime.startTime}")
            }
        }


        var choice: Int

        do {
            println("Выберите действие:")
            println("1. Зарегистрировать пользователя")
            println("2. Войти в систему")
            println("3. Добавить фильм")
            println("4. Удалить фильм")
            println("5. Редактировть фильм")
            println("6. Продать билет")
            println("7. Добавить сеанс")
            println("8. Удалить сеанс")
            println("9. Редактировать сеанс")
            println("10. Вернуть билет")
            println("11. Выйти из кинотеатра")

            try {
                choice = readlnOrNull()?.toInt() ?: 0

                when (choice) {
                    1 -> {
                        println("Введите имя пользователя:")
                        val username = readlnOrNull() ?: ""
                        println("Введите пароль:")
                        val password = readlnOrNull() ?: ""
                        if (registerUser(username, password)) {
                            println("Пользователь зарегистрирован успешно!")
                        } else {
                            println("Ошибка регистрации пользователя.")
                        }
                    }

                    2 -> {
                        println("Введите имя пользователя:")
                        val username = readlnOrNull() ?: ""
                        println("Введите пароль:")
                        val password = readlnOrNull() ?: ""
                        if (loginUser(username, password)) {
                            println("Вход выполнен успешно!")

                        } else {
                            println("Ошибка входа. Проверьте имя пользователя и пароль.")
                        }
                    }

                    3 -> {
                        println("Введите название фильма:")
                        val title = readlnOrNull() ?: ""
                        println("Введите жанр:")
                        val genre = readlnOrNull() ?: ""
                        println("Введите режиссера:")
                        val director = readlnOrNull() ?: ""
                        println("Введите год выпуска:")
                        val releaseYear = readlnOrNull()?.toIntOrNull() ?: 0
                        println("Введите длительность (в минутах):")
                        val duration = readlnOrNull()?.toIntOrNull() ?: 0

                        if (addMovie(title, genre, director, releaseYear, duration)) {
                            println("Фильм добавлен успешно!")
                        } else {
                            println("Ошибка добавления фильма.")
                        }
                    }

                    4 -> {
                        val movie: Movie? = selectMovieFromList()

                        if (removeMovie(movie)) {
                            println("Фильм удален успешно!")
                        } else {
                            println("Ошибка удаления фильма.")
                        }
                    }

                    5 -> {
                        val selectedMovie: Movie? = selectMovieFromList()
                        // Выводим текущие данные фильма
                        println("Текущие данные фильма:")
                        println(selectedMovie)

                        // Предлагаем пользователю внести изменения
                        println("Введите новое название фильма:")
                        val newTitle = readlnOrNull() ?: ""

                        println("Введите новый жанр:")
                        val newGenre = readlnOrNull() ?: ""

                        println("Введите нового режиссера:")
                        val newDirector = readlnOrNull() ?: ""

                        println("Введите новый год выпуска:")
                        val newReleaseYear = readlnOrNull()?.toIntOrNull() ?: 0

                        println("Введите новую длительность фильма (в минутах):")
                        val newDurationMinutes = readlnOrNull()?.toIntOrNull() ?: 0
                        if (editMovie(
                                selectedMovie,
                                newTitle,
                                newGenre,
                                newDirector,
                                newReleaseYear,
                                newDurationMinutes
                            )
                        ) {
                            println("Фильм успешно отредактирован.")
                        } else {
                            println("Ошибка редактирования фильма.")
                        }
                    }

                    6 -> {
                        val movie: Movie? = selectMovieFromList()


                        // Получаем список доступных сеансов для выбранного фильма
                        val availableShowtimes = getAvailableShowtimes(movie)

                        // Выводим список сеансов с номерами
                        availableShowtimes.forEachIndexed { index, showtime ->
                            println("${index + 1}. ${showtime.startTime}")
                        }

                        println("Выберите номер сеанса:")
                        val selectedShowtimeNumber = readlnOrNull()?.toIntOrNull() ?: 0

                        // Проверяем валидность выбора сеанса
                        if (selectedShowtimeNumber in 1..availableShowtimes.size) {
                            val selectedShowtime = availableShowtimes[selectedShowtimeNumber - 1]
                            selectedShowtime.displayHallSchema()
                            println("Введите номер места:")

                            // Пользователь вводит номер ряда и места
                            val seatNumber = readlnOrNull()?.toIntOrNull() ?: 0


                            val row = (seatNumber - 1) / selectedShowtime.availableSeats[0].size
                            val seatInRow = (seatNumber - 1) % selectedShowtime.availableSeats[0].size

                            if (sellTicket(selectedShowtime, row, seatInRow)) {
                                println("Билет продан успешно!")
                            } else {
                                println("Ошибка продажи билета.")
                            }

                        } else {
                            println("Неверный выбор сеанса.")
                        }
                    }

                    7 -> {
                        val movie: Movie? = selectMovieFromList()
                        println("Введите время начала сеанса:")
                        val startTime = readlnOrNull() ?: ""
                        println("Введите длительность сеанса (в минутах):")
                        val duration = readlnOrNull()?.toIntOrNull() ?: 0
                        println("Введите количество доступных мест:")
                        val availableSeats: Array<Array<Boolean>> = Array(3) { Array(3) { true } }
                        val hallSchema: Array<Array<Boolean>> = Array(3) { Array(3) { true } }
                        if (addShowtime(movie, startTime, duration, availableSeats, hallSchema)) {
                            println("Сеанс добавлен успешно!")
                        } else {
                            println("Ошибка добавления сеанса.")
                        }
                    }

                    8 -> {
                        // Вывести список всех сеансов
                        displayShowtimes()
                        println("Введите номер сеанса для удаления:")
                        val selectedShowtimeNumber = readLine()?.toIntOrNull() ?: 0
                        if (selectedShowtimeNumber in 1..showtimes.size) {
                            val selectedShowtime = showtimes[selectedShowtimeNumber - 1]
                            if (removeShowtime(selectedShowtime.movie.title, selectedShowtime.startTime)) {
                                println("Сеанс удален успешно!")
                            } else {
                                println("Ошибка удаления сеанса.")
                            }
                        } else {
                            println("Неверный номер сеанса.")
                        }
                    }

                    9 -> {
                        displayShowtimes()
                        println("Введите номер сеанса для удаления:")
                        val selectedShowtimeNumber = readlnOrNull()?.toIntOrNull() ?: 0

                        if (selectedShowtimeNumber in 1..showtimes.size) {
                            var showtimeToEdit = showtimes[selectedShowtimeNumber - 1]

                            println("Текущие данные сеанса:")
                            println(showtimeToEdit)

                            println("Введите новое время начала сеанса:")
                            val newStartTime = readlnOrNull() ?: ""

                            println("Введите новую длительность сеанса (в минутах):")
                            val newDurationMinutes = readlnOrNull()?.toIntOrNull() ?: 0

                            if (editShowtime(showtimeToEdit, newStartTime, newDurationMinutes)) {
                                println("Сеанс успешно отредактирован.")
                            } else {
                                println("Ошибка редактирования сеанса.")
                            }
                        }
                    }

                    10 -> {
                        displayShowtimes()
                        println("Введите номер сеанса для удаления:")
                        val selectedShowtimeNumber = readlnOrNull()?.toIntOrNull() ?: 0
                        if (selectedShowtimeNumber in 1..showtimes.size) {
                            var selectedShowtime = showtimes[selectedShowtimeNumber - 1]
                            selectedShowtime.displayHallSchema()
                            println("Введите номер места:")
                            val seatNumber = readlnOrNull()?.toIntOrNull() ?: 0
                            val row = (seatNumber - 1) / selectedShowtime.availableSeats[0].size
                            val seatInRow = (seatNumber - 1) % selectedShowtime.availableSeats[0].size
                            if (returnTicket(selectedShowtime, row, seatInRow)) {
                                println("Билет успешно возвращен.")
                            } else {
                                println("Ошибка возврата билета.")
                            }
                        }
                    }

                    11 -> {
                        println("Выход из кинотеатра")
                    }

                    else -> {
                        println("Неверный выбор. Пожалуйста, выберите еще раз.")
                    }
                }
            } catch (e: NumberFormatException) {
                println("Ошибка ввода. Пожалуйста, введите число.")
                choice = 0
            }
            saveDataToFiles()
        } while (choice != 11)
    }
}
