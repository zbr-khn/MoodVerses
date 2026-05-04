const verses = {
    happy: [
       "Surah Ad-Duha (93:5): And your Lord is going to give you, and you will be satisfied.",
       "Surah An-Nahl (16:97): Whoever does righteousness, whether male or female, while he is a believer, We will surely cause him to live a good life...",
       "Surah Ash-Sharh (94:7-8): So when you have finished [your duties], then stand up [for worship]. And to your Lord direct [your] longing.",
       "Surah Al-Fath (48:4): It is He who sent down tranquility into the hearts of the believers that they would increase in faith along with their [present] faith.",
       "Surah Az-Zumar (39:61): And Allah will deliver those who feared Him to their place of salvation; no evil will touch them, nor will they grieve.",
       "Surah Al-‘Imran (3:139): So do not weaken and do not grieve, and you will be superior if you are [true] believers.",
       "Surah Yunus (10:62-64): Unquestionably, for the allies of Allah, there will be no fear concerning them, nor will they grieve. For them are good tidings in the worldly life and in the Hereafter.",
       "Surah Al-Ma’idah (5:119): ... Allah will say, 'This is the Day when the truthful will benefit from their truthfulness.' For them are gardens [in Paradise] beneath which rivers flow, wherein they will abide forever...",
       "Surah Al-Baqarah (2:286): Allah does not burden a soul beyond that it can bear...",
       "Surah Fussilat (41:30): Indeed, those who have said, 'Our Lord is Allah' and then remained steadfast—the angels will descend upon them, [saying], 'Do not fear and do not grieve but receive good tidings of Paradise, which you were promised.' ",
       "Surah Al-Isra (17:9): Indeed, this Qur'an guides to that which is most suitable and gives good tidings to the believers who do righteous deeds that they will have a great reward.",
       "Surah Yunus (10:26): For those who have done good is the best [reward] and extra. No darkness will cover their faces, nor humiliation. Those are companions of Paradise; they will abide therein eternally.",
       "Surah Al-Baqarah (2:155): And We will surely test you with something of fear and hunger and a loss of wealth and lives and fruits, but give good tidings to the patient.",
       "Surah Al-Baqarah (2:82): And whoever fears Allah - He will make for him a way out and will provide for him from where he does not expect.",
       "Surah At-Talaq (65:2-3): But they who believe and do righteous deeds - those are the companions of Paradise; they will abide therein eternally."
    ],
    sad: [
        "Surah Al-Baqarah (2:286): Allah does not burden a soul beyond that it can bear.",
        "Surah Al-Inshirah (94:5-6): For indeed, with hardship [will be] ease. Indeed, with hardship [will be] ease.",
        "Surah Yusuf (12:86): ... I only complain of my suffering and my grief to Allah, and I know from Allah that which you do not know.",
        "Surah At-Tawbah (9:40): ... Do not grieve; indeed Allah is with us.",
        "Surah Al-An’am (6:17): ... And if Allah should touch you with adversity, there is no remover of it except Him.",
        "Surah Al-Baqarah (2:155): And We will surely test you with something of fear and hunger and a loss of wealth and lives and fruits, but give good tidings to the patient.",
        "Surah Al-Kahf (18:28): ... And do not obey one whose heart We have made heedless of Our remembrance and who follows his desire and whose affair is ever [in] neglect.",
        "Surah Al-Hijr (15:97-99): And We already know that your breast is constrained by what they say. So exalt [Allah] with praise of your Lord and be of those who prostrate [to Him]. And worship your Lord until there comes to you the certainty (death).",
        "Surah Az-Zumar (39:53): Say, 'O My servants who have transgressed against themselves [by sinning], do not despair of the mercy of Allah. Indeed, Allah forgives all sins. Indeed, it is He who is the Forgiving, the Merciful.'",
        "Surah Al-Anbiya (21:83-84): And [mention] Job, when he called to his Lord, 'Indeed, adversity has touched me, and you are the Most Merciful of the merciful.' So We responded to him and removed what afflicted him of adversity.",
        "Surah Al-Baqarah (2:155): And We will surely test you with something of fear and hunger and a loss of wealth and lives and fruits, but give good tidings to the patient.",
        "Surah At-Talaq (65:4): And whoever fears Allah - He will make for him of his matter ease.",
        "Surah An-Nahl (16:110): Then, indeed your Lord - to those who emigrated after they had been compelled [to renounce Islam] and thereafter fought [for the cause of Allah] and were patient - indeed, your Lord, after that, is Forgiving and Merciful.",
        "Surah Hud (11:115): And be patient, for indeed, Allah does not allow to be lost the reward of those who do good.",
        "Surah Ad-Duha (93:5): And your Lord is going to give you, and you will be satisfied."
    ],
    lonely: [
       "Surah Ad-Duha (93:3): Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].",
       "Surah Al-Baqarah (2:214): Or do you think that you will enter Paradise while such [trial] has not yet come to you as came to those who passed on before you?...",
       "Surah At-Tawbah (9:40): ... Do not grieve; indeed Allah is with us...",
       "Surah Al-An’am (6:59): And with Him are the keys of the unseen; none knows them except Him...",
       "Surah Al-Hadid (57:4): ... And He is with you wherever you are. And Allah, of what you do, is Seeing.",
       "Surah Maryam (19:24-26): ... 'Do not grieve; your Lord has provided beneath you a stream...",
       "Surah An-Nahl (16:70): And Allah created you; then He will take you in death. And among you is he who is reversed to the most decrepit [old] age so that he will not know, after [having had] knowledge, a thing...",
       "Surah Yusuf (12:87): ... Indeed, no one despairs of relief from Allah except the disbelieving people.",
       "Surah Ta-Ha (20:46): ... Do not fear. Indeed, I am with you both; I hear and I see.",
       "Surah Al-Furqan (25:63): And the servants of the Most Merciful are those who walk upon the earth easily, and when the ignorant address them [harshly], they say [words of] peace.",
       "Surah Ad-Duha (93:7): And He found you lost and guided [you].",
       "Surah Ar-Rum (30:60): So be patient. Indeed, the promise of Allah is truth. And let them not disquiet you who are not certain [in faith].",
       "Surah At-Talaq (65:4): And whoever fears Allah - He will make for him of his matter ease.",
       "Surah Hud (11:115): And be patient, for indeed, Allah does not allow to be lost the reward of those who do good.",
       "Surah Ash-Shu'ara (26:62): He said, 'No! Indeed, with me is my Lord; He will guide me.'"
    ],
    thankful: [
        "Surah Ibrahim (14:7): And [remember] when your Lord proclaimed, 'If you are grateful, I will surely increase you [in favor]...'",
        "Surah Al-Baqarah (2:152): So remember Me; I will remember you. And be grateful to Me and do not deny Me.",
        "Surah Al-‘Imran (3:145): ... And whoever desires the reward of this world, We will give him thereof; and whoever desires the reward of the Hereafter, We will give him thereof. And We will reward the grateful.",
        "Surah Luqman (31:12): And We had certainly given Luqman wisdom [and said], 'Be grateful to Allah.' And whoever is grateful is grateful for himself...",
        "Surah Al-Ankabut (29:17): You worship besides Allah only idols, and you invent [a lie]. Indeed, those who worship idols are disbelievers in Allah’s bounty.",
        "Surah Al-Baqarah (2:261): The example of those who spend their wealth in the way of Allah is like a grain of corn that sprouts seven ears; in each ear is a hundred grains...",
        "Surah Al-Baqarah (2:268): Satan threatens you with poverty and orders you to immorality, while Allah promises you forgiveness from Himself and bounty. And Allah is Munificent and Knowing.",
        "Surah Al-Ma’idah (5:118): If You punish them - indeed, they are Your servants; and if You forgive them - indeed, it is You who is the Exalted in Might, the Wise.",
        "Surah Az-Zumar (39:10): Say, 'O My servants who have believed, fear your Lord. For those who do good in this world is good, and the earth of Allah is spacious. Indeed, the patient will be given their reward without account.'",
        "Surah Al-‘Imran (3:171): Rejoicing in a bounty from Allah and a favor, and that Allah does not allow the reward of the believers to be lost.",
        "Surah Al-Mumtahina (60:8): Allah does not forbid you from those who do not fight you because of religion and do not expel you from your homes - from being righteous toward them and acting justly toward them. Indeed, Allah loves those who act justly.",
        "Surah Al-Ankabut (29:17): You worship besides Allah only idols, and you invent [a lie]. Indeed, those who worship idols are disbelievers in Allah’s bounty.",
        "Surah Al-Ankabut (29:63): And who is it that guides you, except the One who answers the call of the distressed when he calls upon Him and removes evil and makes you inheritors of the earth? Little do you remember.",
        "Surah Al-Isra (17:18): Whoever desires the life of this world and its adornments - We fully repay them for their deeds therein, and they therein will not be deprived.",
        "Surah Al-Mumtahina (60:8): Allah does not forbid you from those who do not fight you because of religion and do not expel you from your homes - from being righteous toward them and acting justly toward them. Indeed, Allah loves those who act justly."
    ],
    angry: [
        "Surah Ash-Shura (42:37): ... And when they are angry, they forgive.",
        "Surah Al-Mumtahina (60:8): Allah does not forbid you from those who do not fight you because of religion and do not expel you from your homes - from being righteous toward them and acting justly toward them. Indeed, Allah loves those who act justly.",
        "Surah Al-Isra (17:37): And do not walk upon the earth exultantly. Indeed, you will never tear the earth [apart], and you will never reach the mountains in height.",
        "Surah Al-Hadid (57:23): So that you may not grieve for what has escaped you nor rejoice for what has been given to you. And Allah does not like every self-deluded, boastful person.",
        "Surah Al-Furqan (25:63): And the servants of the Most Merciful are those who walk upon the earth easily, and when the ignorant address them [harshly], they say [words of] peace.",
        "Surah Al-Ma’idah (5:8): O you who have believed, be persistently standing firm in justice, witnesses for Allah, even if it be against yourselves or parents and relatives.",
        "Surah Al-Ma’idah (5:32): ... And whoever saves one [life] - it is as if he had saved mankind entirely...",
        "Surah Al-Furqan (25:63): And the servants of the Most Merciful are those who walk upon the earth easily, and when the ignorant address them [harshly], they say [words of] peace.",
        "Surah Al-Ma’idah (5:8): O you who have believed, be persistently standing firm in justice, witnesses for Allah, even if it be against yourselves or parents and relatives.",
        "Surah Al-Isra (17:37): And do not walk upon the earth exultantly. Indeed, you will never tear the earth [apart], and you will never reach the mountains in height.",
        "Surah Al-Hadid (57:23): So that you may not grieve for what has escaped you nor rejoice for what has been given to you. And Allah does not like every self-deluded, boastful person.",
        "Surah Al-Furqan (25:63): And the servants of the Most Merciful are those who walk upon the earth easily, and when the ignorant address them [harshly], they say [words of] peace.",
        "Surah Al-Mumtahina (60:8): Allah does not forbid you from those who do not fight you because of religion and do not expel you from your homes - from being righteous toward them and acting justly toward them. Indeed, Allah loves those who act justly.",
        "Surah Al-Furqan (25:63): And the servants of the Most Merciful are those who walk upon the earth easily, and when the ignorant address them [harshly], they say [words of] peace.",
        "Surah Al-Ankabut (29:63): And who is it that guides you, except the One who answers the call of the distressed when he calls upon Him and removes evil and makes you inheritors of the earth? Little do you remember."
    ],
    anxious: [
        "Surah At-Tawbah (9:51): Say, 'Never will we be struck except by what Allah has decreed for us; He is our protector.' And upon Allah let the believers rely.",
        "Surah Al-Ankabut (29:62): Allah extends provision for whom He wills and restricts [it]. Indeed, in that are signs for a people who believe.",
        "Surah Al-Isra (17:99): Do not they see that Allah, who created the heavens and the earth and did not tire of creating them, is able to give life to the dead? Yes, indeed. He is over all things competent.",
        "Surah Al-Ankabut (29:69): And those who strive for Us - We will surely guide them to Our ways. And indeed, Allah is with the doers of good.",
        "Surah At-Tawbah (9:51): Say, 'Never will we be struck except by what Allah has decreed for us; He is our protector.' And upon Allah let the believers rely.",
        "Surah Al-Ankabut (29:62): Allah extends provision for whom He wills and restricts [it]. Indeed, in that are signs for a people who believe.",
        "Surah Al-Isra (17:99): Do not they see that Allah, who created the heavens and the earth and did not tire of creating them, is able to give life to the dead? Yes, indeed. He is over all things competent.",
        "Surah Al-Ankabut (29:69): And those who strive for Us - We will surely guide them to Our ways. And indeed, Allah is with the doers of good.",
        "Surah At-Tawbah (9:51): Say, 'Never will we be struck except by what Allah has decreed for us; He is our protector.' And upon Allah let the believers rely.",
        "Surah Al-Ankabut (29:62): Allah extends provision for whom He wills and restricts [it]. Indeed, in that are signs for a people who believe.",
        "Surah Al-Isra (17:99): Do not they see that Allah, who created the heavens and the earth and did not tire of creating them, is able to give life to the dead? Yes, indeed. He is over all things competent.",
        "Surah Al-Ankabut (29:69): And those who strive for Us - We will surely guide them to Our ways. And indeed, Allah is with the doers of good.",
        "Surah At-Tawbah (9:51): Say, 'Never will we be struck except by what Allah has decreed for us; He is our protector.' And upon Allah let the believers rely.",
        "Surah Al-Ankabut (29:62): Allah extends provision for whom He wills and restricts [it]. Indeed, in that are signs for a people who believe.",
        "Surah Al-Isra (17:99): Do not they see that Allah, who created the heavens and the earth and did not tire of creating them, is able to give life to the dead? Yes, indeed. He is over all things competent."
    ]
};

const verseBox = document.getElementById('verseBox');

function getRandomVerse(emotion) {
    const verseArray = verses[emotion];
    const randomIndex = Math.floor(Math.random() * verseArray.length);
    return verseArray[randomIndex];
}

document.querySelectorAll('.emotion-btn').forEach(button => {
    button.addEventListener('click', () => {
        const emotion = button.id.replace('Btn', '');
        const verse = getRandomVerse(emotion);
        verseBox.innerText = verse;
        verseBox.className = `verse-box ${emotion}-box`;
    });
});
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js').then((registration) => {
            console.log('Service Worker registered with scope:', registration.scope);
        }).catch((error) => {
            console.log('Service Worker registration failed:', error);
        });
    });
}
self.addEventListener('install', event => {
    event.waitUntil(
      caches.open('your-cache-name').then(cache => {
        return cache.addAll([
          '/',
          '/index.html',
          '/styles.css',
          '/script.js',
          '/icons/logo.png'
        ]).catch(error => {
          console.error('Failed to cache resources:', error);
        });
      })
    );
  });
  
