package com.rabpadev.app
import java.util.Calendar
object RandomData {
    private val MALE_NAMES = listOf("Budi","Andi","Rizky","Fajar","Dian","Bagas","Hendra","Agus","Reza","Imam","Wahyu","Yoga","Doni","Eko","Fuad","Galih","Heru","Ivan","Joko","Krisna","Lukman","Miko","Nanda","Okta","Panji")
    private val FEMALE_NAMES = listOf("Sari","Dewi","Ayu","Putri","Indah","Maya","Rina","Wulan","Yuni","Zahra","Aulia","Bella","Citra","Dina","Elsa","Fina","Gita","Hana","Ira","Jeni","Kartika","Lina","Mira","Nita","Olive")
    private val LAST_NAMES = listOf("Santoso","Wijaya","Kusuma","Pratama","Saputra","Nugroho","Hidayat","Firmansyah","Rahayu","Wibowo","Susanto","Hartono","Setiawan","Kurniawan","Gunawan","Lestari","Anggraini","Permatasari","Handayani","Sukardi")
    private val ADJECTIVES = listOf("cool","super","real","best","true","nice","good","top","pro","max","hot","big","fast","smart","free")
    fun randomName(): String {
        val names = MALE_NAMES + FEMALE_NAMES
        return "${names.random()} ${LAST_NAMES.random()}"
    }
    fun randomUsername(): String {
        val base = (MALE_NAMES + FEMALE_NAMES).random().lowercase()
        val num = (10..999).random()
        val adj = if (Math.random() > 0.5) "${ADJECTIVES.random()}_" else ""
        return "${adj}${base}${num}"
    }
    fun randomBirthday(ageMin: Int, ageMax: Int): String {
        val age = (ageMin..ageMax).random()
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -age)
        cal.set(Calendar.MONTH, (0..11).random())
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, (1..maxDay).random())
        val d = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2,'0')
        val m = (cal.get(Calendar.MONTH)+1).toString().padStart(2,'0')
        val y = cal.get(Calendar.YEAR)
        return "$d/$m/$y"
    }
    fun birthdayToYear(birthday: String): String {
        return birthday.split("/").lastOrNull() ?: ""
    }
}