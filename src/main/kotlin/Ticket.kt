data class Ticket(
    val showtime: Showtime,
    val seat: Seat,
    val price: Double,
    /* другие свойства билета */
) {
    companion object {
        private val tickets = mutableListOf<Ticket>()

        // Метод для поиска билета по сеансу и месту
        private fun findTicket(showtime: Showtime, seat: Seat): Ticket? {
            return tickets.find { it.showtime == showtime && it.seat == seat }
        }
    }

//    fun sellTicket(): Boolean {
//        val seatAvailable = showtime.reserveSeat(seat.row, seat.number)
//        if (seatAvailable) {
//            tickets.add(this)
//            return true
//        }
//        return false
//    }

    fun returnTicket(): Boolean {
        showtime.cancelReservation(seat.row, seat.number)
        tickets.remove(this)
        return true
    }

    // Другие методы для работы с данными билета
}
