package com.moodverses.app.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val citations: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)

data class KnowledgeEntry(
    val id: String,
    val title: String,
    val keywords: List<String>,
    val answer: String,
    val citations: List<String>,
    val suggestions: List<String>
)

typealias NoorAiAgent = WaleeyAiAgent

object WaleeyAiAgent {

    /**
     * ══════════════════════════════════════════════════════════════════════════
     * 🔑 APP-WIDE BUILT-IN GEMINI API KEY
     * ══════════════════════════════════════════════════════════════════════════
     * Embedded directly for the entire app so that any user downloading or
     * running the app gets full, instant access to the AI without needing
     * to enter any key.
     * 
     * You can paste your key below or in local.properties:
     * GEMINI_API_KEY=AIzaSy...
     * ══════════════════════════════════════════════════════════════════════════
     */
    var GEMINI_API_KEY: String = com.moodverses.app.BuildConfig.GEMINI_API_KEY.ifBlank { "" }

    val starterPrompts = listOf(
        "Prophet's ﷺ Mercy at Ta'if",
        "How the Quran was compiled",
        "Attaining Khushu in Salah",
        "Virtues of Tahajjud",
        "Islamic Golden Age & Scholars",
        "Overcoming anxiety & grief",
        "The 4 Caliphs (Khulafa)",
        "Sincere Repentance (Tawbah)"
    )

    // Rate limiting: Maximum 8 requests per minute, minimum 2.5s between requests
    private val requestTimestamps = mutableListOf<Long>()
    private const val MAX_REQUESTS_PER_MINUTE = 8
    private const val MIN_INTERVAL_MS = 2500L
    private var lastRequestTime = 0L

    @Synchronized
    private fun checkRateLimit(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastRequestTime < MIN_INTERVAL_MS) {
            return false
        }
        requestTimestamps.removeAll { now - it > 60_000L }
        if (requestTimestamps.size >= MAX_REQUESTS_PER_MINUTE) {
            return false
        }
        lastRequestTime = now
        requestTimestamps.add(now)
        return true
    }

    /**
     * Sanitizes user input: strips control characters, limits length to 400 chars,
     * prevents script injection, and normalizes whitespace.
     */
    fun sanitizeInput(input: String): String {
        return input.take(400)
            .filter { it == '\n' || it == '\t' || !it.isISOControl() }
            .replace(Regex("<[^>]*>"), "") // Strip HTML/script tags
            .trim()
    }

    private val knowledgeBase = listOf(
        KnowledgeEntry(
            id = "taif_mercy",
            title = "The Prophet's ﷺ Mercy at Ta'if",
            keywords = listOf("taif", "mercy", "stoned", "angels", "patience", "forgiveness", "mountain", "suffering"),
            answer = """In the 10th year of Prophethood (the Year of Sorrow), after losing both Khadijah (RA) and Abu Talib, Prophet Muhammad ﷺ walked to Ta'if to invite the Thaqif tribe to Islam. 

Instead of welcoming him, the leaders set upon him street rowdies and youth who pelted him with stones until his sandals were soaked with blood. Seeking refuge in an orchard, he made his famous, heart-wrenching supplication:
*"O Allah, unto You do I complain of my weakness, of my helplessness, and of my insignificance before people..."*

The Angel Jibreel (AS) descended alongside the Angel of Mountains, who asked permission to crush the city between the two surrounding mountains (Al-Akhshabayn). 

The Prophet ﷺ replied with timeless mercy:
*"No, rather I hope that Allah will bring forth from their loins descendants who will worship Allah alone without associating any partners with Him."*

✨ **Lesson**: Never let the cruelty of others extinguish your compassion. Even in his deepest pain, Rasulullah ﷺ prayed for the guidance of his persecutors.""",
            citations = listOf("Sahih al-Bukhari 3231", "Sahih Muslim 1795"),
            suggestions = listOf("Conquest of Makkah", "The Year of Sorrow", "Treaty of Hudaybiyyah")
        ),
        KnowledgeEntry(
            id = "quran_compilation",
            title = "Compilation of the Holy Quran",
            keywords = listOf("quran", "compilation", "compiled", "uthman", "abu bakr", "zaid ibn thabit", "mushaf", "manuscript", "preserved"),
            answer = """The preservation and compilation of the Quran unfolded through three meticulously documented historical stages:

1. **During the Lifetime of the Prophet ﷺ (Oral & Inscribed Records)**:
   Whenever an Ayah was revealed, the Prophet ﷺ designated official scribes (Kuttab al-Wahy)—including Zaid ibn Thabit, Ali ibn Abi Talib, and Ubayy ibn Ka'b—to write it down on parchment, leather, date palm leaves, and shoulder blades, while thousands of Sahabah committed every verse to memory.

2. **The Era of Caliph Abu Bakr As-Siddiq (RA)**:
   Following the Battle of Yamama (632 CE) where many Huffaz were martyred, Umar (RA) proposed gathering all manuscripts into a single codex. Abu Bakr appointed Zaid ibn Thabit (RA) to lead the task. Zaid demanded two eyewitnesses for every written piece to confirm it was inscribed before the Prophet ﷺ. This primary collection was safeguarded with Hafsah bint Umar (RA).

3. **The Era of Caliph Uthman ibn Affan (RA) (Standardization)**:
   As Islam spread from Egypt to Central Asia, regional dialect differences emerged in recitation. In 650 CE, Caliph Uthman (RA) formed a committee headed by Zaid ibn Thabit to transcribe standard copies based on the original dialect of the Quraysh. Copies were dispatched to major capitals (Kufa, Basra, Damascus, Makkah, Madinah).

✨ **Divine Guarantee**: *"Indeed, it is We who sent down the Qur'an and indeed, We will be its guardian."* [Surah Al-Hijr 15:9]""",
            citations = listOf("Surah Al-Hijr 15:9", "Sahih al-Bukhari 4986, 4987"),
            suggestions = listOf("Who was Zaid ibn Thabit?", "The 4 Caliphs (Khulafa)", "Surah Al-Kahf background")
        ),
        KnowledgeEntry(
            id = "khushu_salah",
            title = "Attaining Khushu' (Devotion) in Salah",
            keywords = listOf("khushu", "focus", "salah", "prayer", "distraction", "mindfulness", "concentrate", "presence"),
            answer = """Khushu' (خُشُوع) is the humble presence of heart and stillness of mind in prayer. The Quran describes the believers: *"Successful indeed are the believers: those who are humble in their prayers."* [Surah Al-Mu'minun 23:1-2]

Practical steps to nurture Khushu':
1. **Pre-Prayer Transition (Al-Wuquf)**: Don't rush into Salah directly from checking your phone or running errands. Perform Wudu mindfully, feeling sins washed away with each drop.
2. **Understand What You Recite**: Reflect upon Al-Fatihah. Remember that Allah responds to you personally verse by verse (as narrated in the Hadith Qudsi of Surah Al-Fatihah).
3. **Pace and Tranquility (Tuma'ninah)**: Give every posture (Ruku', Sujud, Standing) its full right. Pause for a breath after each motion.
4. **Treat Each Prayer as Your Last**: The Prophet ﷺ advised: *"When you stand for prayer, pray like a person saying their farewell."*
5. **Seek Refuge from Waswas**: If wandering thoughts invade, recite *A'udhu billahi minash-shaytanir-rajim* quietly and spit lightly dryly three times to your left (as taught to the companion Uthman ibn Abi al-As).""",
            citations = listOf("Surah Al-Mu'minun 23:1-2", "Sunan Ibn Majah 4171", "Sahih Muslim 2203"),
            suggestions = listOf("Virtues of Tahajjud", "Conditions of accepted Dua", "Overcoming anxiety & grief")
        ),
        KnowledgeEntry(
            id = "tahajjud_virtues",
            title = "The Virtues & Beauty of Tahajjud (Night Prayer)",
            keywords = listOf("tahajjud", "night prayer", "qiyam", "last third", "waking up", "night", "dua at night"),
            answer = """The Night Prayer (Tahajjud / Qiyam al-Layl) is the hallmark of the righteous and the most intimate conversation between a servant and the Creator.

Allah subhanahu wa ta'ala says:
*"And from [part of] the night, pray with it as additional [worship] for you; it is expected that your Lord will raise you to a praised station."* [Surah Al-Isra 17:79]

Key virtues of Tahajjud:
- **Divine Descent in the Last Third**: Rasulullah ﷺ said: *"Our Lord descends every night to the lowest heaven when the last third remains, saying: 'Who calls upon Me so that I may answer him? Who asks of Me that I may grant him? Who seeks My forgiveness that I may forgive him?'"* [Sahih al-Bukhari]
- **The Arrow that Never Misses**: Imam Ash-Shafi'i (RA) beautifully said: *"The Dua made at Tahajjud is like an arrow that does not miss its target."*
- **Inner Light & Peace**: Al-Hasan Al-Basri was once asked: *"Why do those who pray Tahajjud have the most radiant faces?"* He replied: *"Because they were alone with the Most Merciful, so He adorned them with His light."*

**How to Pray**:
Wake up in the last third of the night, perform Wudu, and pray 2 rak'ahs at a time with calm reflection, followed by the Witr prayer.""",
            citations = listOf("Surah Al-Isra 17:79", "Sahih al-Bukhari 1145", "Sahih Muslim 758"),
            suggestions = listOf("Conditions of accepted Dua", "Attaining Khushu in Salah", "Sincere Repentance (Tawbah)")
        ),
        KnowledgeEntry(
            id = "islamic_golden_age",
            title = "The Islamic Golden Age & Pioneering Scholars",
            keywords = listOf("golden age", "science", "scholars", "bayt al-hikmah", "khwarizmi", "ibn sina", "optics", "algebra", "history", "civilization"),
            answer = """From the mid-8th to 14th century, the Islamic Golden Age transformed world history by synthesizing science, philosophy, and spirituality:

1. **House of Wisdom (Bayt al-Hikmah) in Baghdad**:
   Founded under the Abbasid Caliphs (Harun al-Rashid & Al-Ma'mun), it served as the world's greatest center of translation, astronomy, and mathematics, translating Greek, Persian, and Indian treatises into Arabic.

2. **Muhammad ibn Musa Al-Khwarizmi (780–850 CE)**:
   - Invented **Algebra** (from his book *Al-Kitab al-Mukhtasar fi Hisab al-Jabr wal-Muqabala*).
   - Introduced Hindu-Arabic numerals and the decimal point to the West; the word **Algorithm** originates directly from his name.

3. **Al-Hasan Ibn al-Haytham (Alhazen) (965–1040 CE)**:
   - Father of **Modern Optics** and pioneer of the **Scientific Method** (empirical observation & falsifiability).
   - Authored the landmark *Kitab al-Manazir* (Book of Optics).

4. **Ibn Sina (Avicenna) (980–1037 CE)**:
   - Authored *Al-Qanun fi al-Tibb* (The Canon of Medicine), which remained Europe's standard medical textbook for over 600 years.

5. **Fatima al-Fihri (859 CE)**:
   - Founded the **University of al-Qarawiyyin** in Fez, Morocco—recognized by UNESCO and Guinness as the oldest continually operating university in human history.

✨ **Spiritual Foundation**: Islamic scientific inquiry flourished because the Quran repeatedly urges humanity: *"Do they not reflect upon the universe and their own creation?"*""",
            citations = listOf("Surah Yunus 10:101", "Surah Ali 'Imran 3:190-191"),
            suggestions = listOf("The 4 Caliphs (Khulafa)", "Who was Imam Bukhari?", "How the Quran was compiled")
        ),
        KnowledgeEntry(
            id = "anxiety_grief",
            title = "Overcoming Anxiety, Sadness & Grief in Islam",
            keywords = listOf("anxiety", "depression", "grief", "sadness", "stress", "worry", "trials", "hardship", "sabr", "tawakkul", "pain"),
            answer = """In Islam, sadness and worry are not marks of spiritual failure. Even the greatest Prophets wept and experienced immense sorrow:
- Prophet Ya'qub (AS) wept for Yusuf (AS) until his eyesight dimmed.
- The Prophet Muhammad ﷺ named the year he lost Khadijah (RA) and Abu Talib *"The Year of Sorrow"*.

Islamic pillars for healing emotional pain:
1. **Validation & Patience (Sabr)**: 
   Sabr is not passive suffering; it is active emotional resilience. Allah reminds us: *"And seek help through patience and prayer..."* [Surah Al-Baqarah 2:45]
2. **Every Pain Expiates Sins**: 
   Rasulullah ﷺ said: *"No fatigue, illness, anxiety, sorrow, harm, or distress befalls a Muslim—even the prick of a thorn—except that Allah expiates some of their sins through it."* [Bukhari]
3. **The Divine Du'a for Anxiety**:
   *«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ»*
   *"O Allah, I seek refuge in You from anxiety and grief, helplessness and laziness, stinginess and cowardice, the burden of debt, and being overpowered by people."* [Bukhari 2893]
4. **Relief is Coupled with Difficulty**:
   *"For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease."* [Surah Ash-Sharh 94:5-6]""",
            citations = listOf("Surah Ash-Sharh 94:5-6", "Surah Al-Baqarah 2:45", "Sahih al-Bukhari 5641, 2893"),
            suggestions = listOf("Prophet's ﷺ Mercy at Ta'if", "Conditions of accepted Dua", "Virtues of Tahajjud")
        ),
        KnowledgeEntry(
            id = "four_caliphs",
            title = "The Rightly Guided Caliphs (Al-Khulafa ar-Rashidun)",
            keywords = listOf("caliphs", "khulafa", "abu bakr", "umar", "uthman", "ali", "rashidun", "companions", "sahabah"),
            answer = """The four Rightly Guided Caliphs guided the Ummah following the demise of the Prophet ﷺ (632–661 CE):

1. **Abu Bakr As-Siddiq (RA) (632–634 CE)**:
   - Epitome of steadfast faith (*Siddiq*).
   - Reunified the Muslim world during the Riddah wars and initiated the gathering of the Quran into one codex.
   - His immortal inauguration speech: *"I have been appointed over you, though I am not the best among you. If I do well, help me; if I do wrong, set me straight."*

2. **Umar ibn Al-Khattab (RA) (634–644 CE) - Al-Faruq**:
   - Giant of justice, administrative genius, and humility.
   - Established the Hijri calendar, the postal system, public treasury (Bayt al-Mal), and led relief during the Great Famine.
   - Famously walked beside his servant taking turns riding their camel when entering Jerusalem peacefully.

3. **Uthman ibn Affan (RA) (644–656 CE) - Dhu an-Nurayn**:
   - Possessor of the Two Lights (married Ruqayyah and Umm Kulthum, daughters of the Prophet ﷺ).
   - Renowned for immense modesty and generosity (bought the well of Rumah for Madinah).
   - Standardized the Mus'haf of the Quran to preserve unity.

4. **Ali ibn Abi Talib (RA) (656–661 CE) - Asadullah (Lion of Allah)**:
   - First youth to accept Islam, renowned for peerless bravery, deep legal rulings, and eloquence.
   - Governed with uncompromising integrity and asceticism throughout civil tribulations.""",
            citations = listOf("Sunan Abi Dawud 4607", "Tirmidhi 3713"),
            suggestions = listOf("How the Quran was compiled", "Islamic Golden Age & Scholars", "Prophet's ﷺ Mercy at Ta'if")
        ),
        KnowledgeEntry(
            id = "tawbah_repentance",
            title = "Sincere Repentance (Tawbah) and Allah's Infinite Mercy",
            keywords = listOf("tawbah", "repentance", "sin", "forgiveness", "guilt", "mercy", "turn back", "istighfar"),
            answer = """In Islam, no sin is too heavy for Allah's forgiveness. Despair of Allah's mercy is explicitly forbidden.

Allah's magnificent invitation:
*"Say, 'O My servants who have transgressed against themselves [by sinning], do not despair of the mercy of Allah. Indeed, Allah forgives all sins. Indeed, it is He who is the Forgiving, the Merciful.'"* [Surah Az-Zumar 39:53]

The 4 Conditions of Sincere Repentance (Tawbah Nasuhah):
1. **Immediate Cessation**: Stop committing the sin immediately.
2. **Deep Remorse (An-Nadam)**: Feeling genuine sorrow in the heart (*"Regret is repentance"* - Prophet ﷺ).
3. **Firm Intention Never to Return**: Resolving sincerely not to repeat the transgression.
4. **Restitution of Rights**: If the sin involved harming another person (stolen property, slander, gossip), one must restore their rights or seek their pardon.

✨ **Prophetic Glad Tidings**: 
*"The one who repents from sin is like one who has no sin at all."* [Sunan Ibn Majah 4250]""",
            citations = listOf("Surah Az-Zumar 39:53", "Sunan Ibn Majah 4250", "Sahih Muslim 2749"),
            suggestions = listOf("Virtues of Tahajjud", "Conditions of accepted Dua", "Attaining Khushu in Salah")
        ),
        KnowledgeEntry(
            id = "dua_acceptance",
            title = "The Etiquette and Conditions of Accepted Dua",
            keywords = listOf("dua", "supplication", "accepted", "conditions", "etiquette", "answering", "when to make dua"),
            answer = """Dua is described by the Prophet ﷺ as *"the essence of worship"* (Tirmidhi 3371). Allah proclaims: *"Call upon Me; I will respond to you."* [Surah Ghafir 40:60]

Key etiquette for answered Dua:
1. **Begin with Praise and Salawat**: Start by praising Allah with His Most Beautiful Names (Asma'ul Husna), and send blessings upon Prophet Muhammad ﷺ.
2. **Certainty of Response (Yaqeen)**: Pray with firm conviction that Allah hears you. Do not say *"O Allah, forgive me if You will."*
3. **Purity of Sustenance**: Ensure food, drink, and income are lawful (Halal).
4. **Golden Times for Dua**:
   - During **Sujud** (*"The closest a servant is to his Lord is while in prostration"*).
   - In the **last third of the night** (Tahajjud).
   - Between the **Adhan and the Iqamah**.
   - In the **last hour of Friday** (Asr to Maghrib).
   - While **traveling** or **fasting**.
   - When **rain falls**.

✨ **How Allah Responds**:
A Dua is answered in one of three ways: (1) Granted in this world, (2) Evil or calamity of equal measure averted, or (3) Saved as radiant reward in the Akhirah.""",
            citations = listOf("Surah Ghafir 40:60", "Jami` at-Tirmidhi 3371", "Musnad Ahmad 11133"),
            suggestions = listOf("Virtues of Tahajjud", "Attaining Khushu in Salah", "Overcoming anxiety & grief")
        ),
        KnowledgeEntry(
            id = "imam_bukhari",
            title = "Imam Al-Bukhari & The Science of Hadith Verification",
            keywords = listOf("bukhari", "hadith", "sunnah", "scholar", "isnad", "authenticity", "sahih"),
            answer = """Muhammad ibn Isma'il al-Bukhari (810–870 CE), born in Bukhara (modern Uzbekistan), revolutionized human historiography through the rigorous science of **Hadith Verification (Mustalah al-Hadith)**.

Key facts about Imam Bukhari's monumental life:
- **Childhood & Memory**: Memorized the entire Quran at a young age and memorized over 70,000 Hadith with their complete chains of transmission (Isnad) as a teenager.
- **The Rigor of His Sahih**: Spent 16 years traveling across Khurasan, Iraq, Hijaz, Syria, and Egypt. Out of approximately 600,000 narrations he collected, he selected only 7,275 (including repetitions) for *Al-Jami' al-Sahih*.
- **Spiritual Method**: For every single Hadith he included in his Sahih, he performed Ghusl (ritual bath), prayed two rak'ahs of Istikharah prayer, and verified both the upright character (*'Adalah*) and unbroken chronological connection (*Ittisal*) of every narrator in the chain.

✨ His work is recognized by consensus of the Muslim Ummah as the most authentic book after the Holy Quran.""",
            citations = listOf("Siyar A'lam al-Nubala by Imam al-Dhahabi", "Muqaddimah Ibn al-Salah"),
            suggestions = listOf("How the Quran was compiled", "Islamic Golden Age & Scholars", "The 4 Caliphs (Khulafa)")
        ),
        KnowledgeEntry(
            id = "fasting_wisdom",
            title = "The Inner Dimensions & Spiritual Wisdom of Fasting",
            keywords = listOf("fasting", "sawm", "ramadan", "hunger", "taqwa", "benefits", "spiritual"),
            answer = """Fasting (Sawm) is prescribed in the Quran with a clear objective:
*"O you who have believed, decreed upon you is fasting as it was decreed upon those before you that you may become righteous (attain Taqwa)."* [Surah Al-Baqarah 2:183]

The Three Levels of Fasting (as described by Imam Al-Ghazali):
1. **Fasting of the General Body (Sawm al-'Umum)**: Refraining from food, drink, and intimate relations from dawn to dusk.
2. **Fasting of the Senses (Sawm al-Khusus)**: Restraining the eyes, tongue, ears, hands, and feet from committing sins (lying, backbiting, anger, gossip). Rasulullah ﷺ warned: *"Whoever does not give up false speech and evil actions, Allah is not in need of his leaving food and drink."* [Bukhari]
3. **Fasting of the Heart (Sawm Khusus al-Khusus)**: Fasting of the inner consciousness from worldly greed and distracting thoughts, centering the heart solely upon Allah.

✨ **Divine Reward**: In a Hadith Qudsi, Allah says: *"Fasting is for Me, and I Myself shall reward it."* [Bukhari]""",
            citations = listOf("Surah Al-Baqarah 2:183", "Sahih al-Bukhari 1903, 1904", "Ihya' 'Ulum al-Din by Al-Ghazali"),
            suggestions = listOf("Attaining Khushu in Salah", "Sincere Repentance (Tawbah)", "Conditions of accepted Dua")
        )
    )

    fun getInitialGreeting(): ChatMessage {
        return ChatMessage(
            text = """Assalamu Alaikum wa Rahmatullahi wa Barakatuh! 🌿

I am **Waleey (وَلِيّ) — Your Islamic AI Companion**, here to accompany your journey through the timeless guidance of the Holy Quran, Prophetic Seerah, and Islamic history.

Ask me anything about:
• **Prophetic Seerah & Milestones** (Ta'if, Badr, Hudaybiyyah)
• **Preservation of the Quran & Hadith** (Zaid ibn Thabit, Imam Bukhari)
• **Spiritual Devotion** (Khushu' in Salah, Tahajjud, Dua)
• **The Islamic Golden Age** (Al-Khwarizmi, Bayt al-Hikmah, Ibn Sina)
• **Navigating Life's Trials** (Sabr, Tawakkul, overcoming grief)

How may I assist your contemplation and learning today?""",
            isUser = false,
            suggestions = starterPrompts.take(4)
        )
    }

    suspend fun processQuery(
        userQuery: String,
        history: List<ChatMessage> = emptyList(),
        customApiKey: String? = null
    ): ChatMessage = withContext(Dispatchers.IO) {
        val sanitized = sanitizeInput(userQuery)
        if (sanitized.isEmpty()) {
            return@withContext ChatMessage(
                text = "Please enter a question or topic regarding Islamic knowledge or history.",
                isUser = false,
                suggestions = starterPrompts.take(3)
            )
        }

        // Rate limiting check
        if (!checkRateLimit()) {
            return@withContext ChatMessage(
                text = "Please pause for a brief moment before sending another inquiry. As Allah reminds us in the Quran:\n\n*«وَاصْبِرْ فَإِنَّ اللَّهَ لَا يُضِيعُ أَجْرَ الْمُحْسِنِينَ»*\n\n*\"And be patient, for indeed Allah does not allow the reward of the doers of good to be lost.\"* [Surah Hud 11:115]",
                isUser = false,
                suggestions = starterPrompts.shuffled().take(3)
            )
        }

        val clean = sanitized.lowercase().trim()

        // Greeting detection (only when user specifically greets)
        if (clean in listOf("salam", "assalam", "assalamu alaikum", "as-salamu alaykum", "hello", "hi", "hey")) {
            return@withContext ChatMessage(
                text = "Wa Alaikum Assalam wa Rahmatullahi wa Barakatuh! 🌸 May peace and blessings of Allah be upon you. How may I assist your learning or reflection today?",
                isUser = false,
                suggestions = starterPrompts.shuffled().take(4)
            )
        }

        // Gratitude detection
        if (clean.contains("jazak") || clean.contains("thank") || clean.contains("shukran")) {
            return@withContext ChatMessage(
                text = "Wa Iyyakum! (وَإِيَّاكُمْ - And may Allah reward you as well). May Allah increase you in beneficial knowledge and steadfastness. Feel free to ask any other questions anytime!",
                isUser = false,
                suggestions = starterPrompts.shuffled().take(4)
            )
        }

        // 1. Try Live Gemini API with conversation history context
        val apiKey = customApiKey?.trim()?.takeIf { it.isNotEmpty() }
            ?: GEMINI_API_KEY.trim().takeIf { it.isNotEmpty() }

        if (apiKey != null) {
            val geminiResponse = callGeminiApi(sanitized, history, apiKey)
            if (geminiResponse != null) {
                return@withContext geminiResponse
            }
        }

        // 2. High-confidence match in curated offline database (only when offline or if direct match)
        var bestEntry: KnowledgeEntry? = null
        var bestScore = 0

        for (entry in knowledgeBase) {
            var score = 0
            for (kw in entry.keywords) {
                if (clean.contains(kw)) {
                    score += 3
                }
            }
            val titleWords = entry.title.lowercase().split(" ")
            for (tw in titleWords) {
                if (tw.length > 3 && clean.contains(tw)) {
                    score += 2
                }
            }
            if (score > bestScore) {
                bestScore = score
                bestEntry = entry
            }
        }

        if (bestEntry != null && bestScore >= 5) {
            return@withContext ChatMessage(
                text = bestEntry.answer,
                isUser = false,
                citations = bestEntry.citations,
                suggestions = bestEntry.suggestions
            )
        }

        // 3. Fallback when network connection cannot reach AI
        return@withContext ChatMessage(
            text = """Seeking knowledge (*Talab al-'Ilm*) is a sacred pursuit in Islam.

I am momentarily unable to connect to the live network to generate a complete and detailed answer for: *"${userQuery.trim()}"*.

Please ensure your internet connection is active, or tap one of the verified foundation topics below:""",
            isUser = false,
            citations = listOf("Surah Muhammad 47:24", "Sahih Muslim 2699"),
            suggestions = starterPrompts.shuffled().take(4)
        )
    }

    private fun callGeminiApi(
        prompt: String,
        history: List<ChatMessage>,
        apiKey: String
    ): ChatMessage? {
        val models = listOf("gemini-3.5-flash", "gemini-2.5-flash")
        for (modelName in models) {
            try {
                val urlString = "https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=$apiKey"
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 20000
                conn.readTimeout = 35000

                val systemInstruction = "You are Waleey (وَلِيّ) — an Islamic AI Companion for the MoodVerses app. Answer questions about Islam, the Holy Quran, Sunnah, Islamic history, and Seerah with authentic citations (Surah:Ayah, Sahih Hadith).\n" +
                        "- CRITICAL RULE: DO NOT start your response with 'Assalamu Alaikum', 'Wa Alaikum Assalam', or any greeting. The Islamic greeting has already been given once at the start of the chat. Jump immediately and directly into answering the question.\n" +
                        "- Provide thorough, detailed, and completely finished explanations. NEVER truncate or stop mid-sentence.\n" +
                        "- Format your answers with clear markdown bullet points and bold headings. Never wrap words in doubled quotation marks like \"\"word\"\"; use standard bold **word** instead."

                val contentsArray = JSONArray()

                // Include the last 6 messages of conversation history for rich context
                val recentHistory = history.filter { !it.text.startsWith("Assalamu Alaikum wa Rahmatullahi") }.takeLast(6)
                for (msg in recentHistory) {
                    val role = if (msg.isUser) "user" else "model"
                    val msgText = msg.text.trim()
                    if (msgText.isNotBlank()) {
                        contentsArray.put(JSONObject().apply {
                            put("role", role)
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", msgText) })
                            })
                        })
                    }
                }

                // Add current prompt
                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })

                val jsonBody = JSONObject().apply {
                    put("system_instruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        })
                    })
                    put("contents", contentsArray)
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.65)
                        put("maxOutputTokens", 2500)
                    })
                }

                OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                    writer.write(jsonBody.toString())
                    writer.flush()
                }

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                    val responseText = reader.readText()
                    reader.close()

                    val rootJson = JSONObject(responseText)
                    val candidates = rootJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val rawReply = parts.getJSONObject(0).optString("text")

                            // Strip any repeated opening greetings and clean quotes
                            var cleanReply = rawReply
                                .replace(Regex("^(Wa\\s*Alaikum\\s*Assalam[^.!?,\\n]*[.!?,\\n]|Assalamu\\s*Alaikum[^.!?,\\n]*[.!?,\\n])", RegexOption.IGNORE_CASE), "")
                                .replace(Regex("[\"“”]{2,}([^\"“”]+)[\"“”]{2,}"), "**$1**")
                                .replace(Regex("'{2,}([^']+)('{2,})"), "*$1*")
                                .trim()

                            return ChatMessage(
                                text = cleanReply,
                                isUser = false,
                                citations = listOf("Google Gemini 3.5 Live Knowledge", "Quran & Sunnah Principles"),
                                suggestions = starterPrompts.shuffled().take(3)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Try next model or fallback
            }
        }
        return null
    }
}
