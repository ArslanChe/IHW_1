import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException

class FileHandler {
    val gson = Gson()

    fun saveDataToFile(data: Any, fileName: String): Boolean {
        val jsonString = gson.toJson(data)

        try {
            File(fileName).writeText(jsonString)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    inline fun <reified T> readFromFile(filename: String): T? {
        try {
            val fileContent = File(filename).bufferedReader().use(BufferedReader::readText)
            val typeToken = object : TypeToken<T>() {}.type
            return gson.fromJson(fileContent, typeToken)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun loadUsersFromFile(filename: String): List<User>? {
        val users: List<User>? = readFromFile(filename)
        return users
    }

    fun loadMoviesFromFile(filename: String): List<Movie>? {
        val movies: List<Movie>? = readFromFile(filename)
        return movies
    }

    fun loadShowtimesFromFile(filename: String): List<Showtime>? {
        val showtimes: List<Showtime>? = readFromFile(filename)
        return showtimes
    }

    fun loadTicketsFromFile(filename: String): List<Ticket>? {
        val tickets: List<Ticket>? = readFromFile(filename)
        return tickets
    }

}
