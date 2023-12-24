data class Ticket(
    val showtime: Showtime,
    val seat: Seat,
    val price: Double,
) {
    companion object {
        private val tickets = mutableListOf<Ticket>()

        // Метод для поиска билета по сеансу и месту
        private fun findTicket(showtime: Showtime, seat: Seat): Ticket? {
            return tickets.find { it.showtime == showtime && it.seat == seat }
        }
    }


    fun returnTicket(): Boolean {
        showtime.cancelReservation(seat.row, seat.number)
        tickets.remove(this)
        return true
    }

}
