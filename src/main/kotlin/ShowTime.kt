data class Seat(val row: Int, val number: Int)

data class Showtime(
    val movie: Movie,
    var startTime: String,
    var durationMinutes: Int,
    val availableSeats: Array<Array<Boolean>>,
    val hallSchema: Array<Array<Boolean>>

) {
    companion object {
        private val showtimes = mutableListOf<Showtime>()

        // Метод для поиска сеанса по названию фильма и времени начала
        private fun findShowtime(movie: Movie, startTime: String): Showtime? {
            return showtimes.find { it.movie == movie && it.startTime == startTime }
        }
    }

    fun addShowtime(): Boolean {
        if (findShowtime(movie, startTime) == null) {
            showtimes.add(this)
            return true
        }
        return false
    }

    fun removeShowtime(): Boolean {
        val existingShowtime = findShowtime(movie, startTime)
        if (existingShowtime != null) {
            showtimes.remove(existingShowtime)
            return true
        }
        return false
    }

    fun reserveSeat(row: Int, number: Int): Boolean {
        if (availableSeats[row][number]){
            availableSeats[row][number] = false
            return true
        }
        return false
    }

    fun cancelReservation(row: Int, number: Int): Boolean {
        availableSeats[row][number] = true
        return true
    }

    fun displayHallSchema() {
        // Выводим схему зала с обозначением свободных и занятых мест
        var k: Int  = 0;
        println("Экран тут")
        for (row in availableSeats.indices) {
            for (seat in availableSeats[row].indices) {
                k += 1
                val seatStatus = if (availableSeats[row][seat]) k else "X"
                print("$seatStatus \t")
            }
            println()
        }
    }

}
