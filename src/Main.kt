import java.time.LocalDateTime
import java.util.UUID

// Абстрактний клас користувача (інкапсуляція)
abstract class Person(val id: String, val name: String) {
    open fun displayInfo() {
        println("ID: $id, Name: $name")
    }
}

// Інтерфейс для типу членства (Bridge)
interface PersonType {
    fun getTypeInfo(): String
}

// Конкретна реалізація типу членства
class MemberType(private val membershipType: String) : PersonType {
    override fun getTypeInfo(): String {
        return "Membership Type: $membershipType"
    }
}

// Клас для учасників спортзалу (з використанням шаблону Builder)
class Member private constructor(
    id: String,
    name: String,
    val personType: PersonType  // Доступне поле для типу членства
) : Person(id, name) {
    var checkInTime: String? = null  // Nullable тип

    override fun displayInfo() {
        super.displayInfo()
        println(personType.getTypeInfo())
    }

    fun renewMembership() {
        println("Membership renewed for member: $name")
    }

    fun checkIn() {
        checkInTime = LocalDateTime.now().toString()
        println("$name checked into the gym at $checkInTime.")
    }

    // Породжуючий шаблон: Builder
    class Builder(private val id: String, private val name: String) {
        private var membershipType: String = "Standard"

        fun setMembershipType(membershipType: String) = apply { this.membershipType = membershipType }

        fun build(): Member {
            return Member(id, name, MemberType(membershipType))
        }
    }
}

// Успадкування для тренера
class Instructor(id: String, name: String, val expertise: String) : Person(id, name) {
    fun scheduleTraining(sessionDetails: String) {
        println("$name scheduled training: $sessionDetails")
    }

    fun conductTraining() {
        println("$name is conducting training in $expertise.")
    }
}

// Колекція учасників спортзалу (Iterator pattern)
class GymManagementSystem {
    private val members = mutableListOf<Member>()

    fun addMember(member: Member) {
        members.add(member)
        println("Member added: ${member.name}")
    }

    // Фільтрація членів за типом абонемента
    fun filterMembersByType(type: String): List<Member> {
        return members.filter { it.personType.getTypeInfo().contains(type, ignoreCase = true) }
    }

    // Сортування учасників за іменем
    fun sortMembersByName() {
        members.sortBy { it.name }
        println("Members sorted by name: ${members.joinToString { it.name }}")
    }

    // Пошук учасника за ім'ям (робота з рядками)
    fun findMemberByName(searchName: String): Member? {
        return members.find { it.name.equals(searchName, ignoreCase = true) }
    }

    // Ітератор для перебору учасників
    fun getMemberIterator(): Iterator<Member> {
        return members.iterator()
    }
}

// Клас сесії доступу (з Nullable типами)
class Session {
    val sessionID: String = UUID.randomUUID().toString()
    val startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime? = null

    fun extend(durationMinutes: Long) {
        endTime = (endTime ?: startTime).plusMinutes(durationMinutes)
        println("Session extended. New end time: $endTime")
    }
}

// Головна функція для тестування
fun main() {
    val gymSystem = GymManagementSystem()

    // Створення учасників за допомогою Builder
    val member1 = Member.Builder("M001", "John Doe").setMembershipType("Premium").build()
    val member2 = Member.Builder("M002", "Jane Smith").setMembershipType("Standard").build()
    val member3 = Member.Builder("M003", "Alice Johnson").setMembershipType("Premium").build()

    // Додавання учасників до системи
    gymSystem.addMember(member1)
    gymSystem.addMember(member2)
    gymSystem.addMember(member3)

    // Фільтрація за типом абонемента
    println("Premium members:")
    gymSystem.filterMembersByType("Premium").forEach { it.displayInfo() }

    // Сортування учасників за іменем
    gymSystem.sortMembersByName()

    // Пошук учасника за ім'ям
    val searchName = "Jane Smith"
    val foundMember = gymSystem.findMemberByName(searchName)
    if (foundMember != null) {
        println("Found member: ${foundMember.name}")
    } else {
        println("Member $searchName not found.")
    }

    // Робота з сесією та Nullable типами
    val session = Session()
    session.extend(60)

    // Використання ітератора
    println("All gym members using iterator:")
    val iterator = gymSystem.getMemberIterator()
    while (iterator.hasNext()) {
        val member = iterator.next()
        member.displayInfo()
    }
}
