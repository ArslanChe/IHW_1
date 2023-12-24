import java.security.MessageDigest
import org.mindrot.jbcrypt.BCrypt

class User(val username: String, var password: String) {

    init {
        password = BCrypt.hashpw("", BCrypt.gensalt())
    }

    companion object {
        private val users = mutableListOf<User>()

        // Метод для шифрования пароля с использованием SHA-256
        private fun encryptPassword(password: String): String {
            val hash = BCrypt.hashpw(password, BCrypt.gensalt())
            return hash
        }

        // Метод для поиска пользователя по логину
        private fun findUserByUsername(username: String): User? {
            return users.find { it.username == username }
        }
    }


    fun authenticate(password: String): Boolean {
        val foundUser = findUserByUsername(username)
        if (foundUser != null) {
            val enteredPasswordHash = encryptPassword(password)
            return foundUser.password == enteredPasswordHash
        }
        return false
    }
}
