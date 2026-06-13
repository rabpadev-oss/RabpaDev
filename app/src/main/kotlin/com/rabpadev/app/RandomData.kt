package com.rabpadev.app
import java.util.Calendar

object RandomData {

    private val FIRST_NAMES = listOf(
        "budi","andi","rizky","fajar","dian","bagas","hendra","agus","reza","imam",
        "wahyu","yoga","doni","eko","galih","heru","ivan","krisna","lucky","mario",
        "sari","dewi","ayu","putri","indah","maya","rina","wulan","yuni","zahra",
        "aulia","bella","citra","dina","elsa","gita","hana","lina","mira","nita",
        "arif","bima","candra","desta","erwin","fandi","haris","ilham","jaka","kevin",
        "umar","vino","widi","yuda","zaki","alya","bintang","cahya","dafa","rafi",
        "gilang","hafiz","irfan","jalal","karim","latif","munir","nabil","omar","pandu",
        "qori","rafi","salma","tari","ulfa","vira","winda","xena","yasmin","zara"
    )

    private val LAST_NAMES = listOf(
        "santoso","wijaya","kusuma","pratama","saputra","nugroho","hidayat",
        "rahayu","wibowo","susanto","hartono","setiawan","kurniawan","gunawan",
        "lestari","anggraini","permata","handayani","sukardi","firmansyah",
        "purnama","utama","perdana","mustika","anugrah","cahyono","prasetyo",
        "sitorus","manurung","lubis","siregar","nasution","harahap","sinaga",
        "putra","putri","ramadan","ramadhan","abdillah","azzahra","nugraha",
        "adiputra","mahendra","ferdiansyah","alfarizi","hamdani","maulana"
    )

    private val MID_NAMES = listOf(
        "dwi","tri","nur","sri","eka","budi","adi","wahyu","rizal","fajar",
        "dani","rendi","andre","putra","putri","siti","ani","bayu","cinta","damai",
        "elok","fikri","graha","harum","indra","jaya","kasih","luqman","mulia","nyoman"
    )

    // Pre-built natural display names (up to 3 words)
    private val DISPLAY_NAMES = listOf(
        "Budi Santoso","Andi Wijaya","Rizky Pratama","Dewi Lestari","Maya Kusuma",
        "Fajar Nugroho","Sari Rahayu","Ayu Permata","Hendra Wibowo","Dian Utama",
        "Bagas Setiawan","Putri Anggraini","Indah Kurniawan","Reza Firmansyah",
        "Wahyu Gunawan","Yoga Susanto","Bella Hartono","Zahra Purnama","Galih Perdana",
        "Mira Mustika","Kevin Anugrah","Lucky Cahyono","Arif Prasetyo","Citra Handayani",
        "Bima Saputra","Desta Hidayat","Erwin Nugroho","Nita Lestari","Alya Rahayu",
        "Rina Sitorus","Hana Manurung","Gita Sinaga","Dafa Nasution","Ilham Harahap",
        "Nur Ayu Lestari","Dwi Budi Santoso","Tri Maya Putri","Sri Dewi Rahayu",
        "Eka Rizky Pratama","Muhammad Fajar","Ahmad Rizal Wijaya","Siti Nur Haliza",
        "Anisa Putri Cantika","Rafi Ahmad Maulana","Gilang Ramadhan Putra",
        "Hafiz Abdillah","Yasmin Azzahra","Salma Nugraha","Pandu Mahendra",
        "Irfan Hamdani","Jalal Maulana","Nabil Alfarizi","Vira Ferdiansyah",
        "Winda Adiputra","Zara Ramadhan","Qori Indah Sari","Ulfa Cinta Damai",
        "Bintang Cahya","Rafi Jaya Putra","Dani Elok Permata","Bayu Luqman Harum"
    )

    fun randomName(): String = DISPLAY_NAMES.random()

    fun randomUsername(): String {
        val f1 = FIRST_NAMES.random()
        val f2 = FIRST_NAMES.random()
        val mid = MID_NAMES.random()
        val last = LAST_NAMES.random()

        return when ((0..6).random()) {
            // Pure name combos - no numbers at all
            0 -> "${f1}${last}"                    // e.g. "rizkywibowo"
            1 -> "${f1}_${last}"                   // e.g. "dewi_lestari"
            2 -> "${f1}.${last}"                   // e.g. "andi.pratama"
            3 -> "${mid}_${f1}"                    // e.g. "nur_ayu"
            4 -> "${mid}.${f1}.${last}"            // e.g. "dwi.budi.santoso"
            5 -> {
                // title_name style
                val title = listOf("mas","mba","kak","bang","bro","sis","si","kak","mr","ms","miss").random()
                "${title}_${f1}_${last}"           // e.g. "mas_andi_pratama"
            }
            else -> "${f1}_${mid}_${f2}"           // e.g. "rizky_dwi_fajar"
        }
    }

    fun randomBirthday(ageMin: Int, ageMax: Int): String {
        val age = (ageMin..ageMax).random()
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -age)
        cal.set(Calendar.MONTH, (0..11).random())
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, (1..maxDay).random())
        val d = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val m = (cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val y = cal.get(Calendar.YEAR)
        return "$d/$m/$y"
    }

    fun birthdayToYear(birthday: String): String = birthday.split("/").lastOrNull() ?: ""
}
