import java.security.MessageDigest
import org.mindrot.jbcrypt.BCrypt

class User(val username: String, var encryptedPassword: String) {

    init {
        encryptedPassword = BCrypt.hashpw(encryptedPassword, BCrypt.gensalt())
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


    fun authenticate(): Boolean {
        val foundUser = findUserByUsername(username)
        if (foundUser != null) {
            val enteredPasswordHash = encryptPassword(encryptedPassword)
            return foundUser.encryptedPassword == enteredPasswordHash
        }
        return false
    }
}
