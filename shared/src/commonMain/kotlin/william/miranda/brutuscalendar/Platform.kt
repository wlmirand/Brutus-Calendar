package william.miranda.brutuscalendar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform