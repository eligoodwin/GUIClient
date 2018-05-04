package TestBot;

import java.util.Random;

public class JadenSmithBot {
    public static final String NAME = "JadenBot";
    private static final String[] terribleTweets = {
            "I Watch Twilight Every Night",
            "I Will Always Give You The Truth I Will Never Lie To You In My music If You Cant Handle My Feelings And Emotions Please Unfollow Me",
            "Once You Go In You Always Come Out Alive",
            "I Only Apply To The Sixth Amendment",
            "Dying Is MainStream #MONEY",
            "Ill Never Forget The Blogs That Believed In Me Since The Begging.",
            "People Tell Me To Smile I Tell Them The Lack Of Emotion In My Face Doesn't Mean I'm Unhappy",
            "Luke. Who Has The Trident",
            "Umm Who Has The Floss",
            "Most Trees Are Blue",
            "You Must Not Know Fashion",
            "I Hope It Doesn't Take For Me To Die For You To See What I Do For You",
            "How Can Mirrors Be Real If Our Eyes Aren't Real",
            "Lately People Call Me Scoop Life",
            "\"It's Your Birthday\" Mateo Said. I Didn't Respond. \"Are You Not Excited To Be 15\" He Asked. Reading My Book I Uttered \"I Turned 15 Long Ago\"",
            "If A Book Store Never Runs Out Of A Certain Book, Dose That Mean That Nobody Reads It, Or Everybody Reads It",
            "All The Rules In This World Were Made By Someone No Smarter Than You. So Make Your Own",
            "If Newborn Babies Could Speak They Would Be The Most Intelligent Beings On Planet Earth",
            "School Is The Tool To Brainwash The Youth",
            "If Everybody In The World Dropped Out Of School We Would Have A Much More Intelligent Society"
    };

    public static String getRandomGarbage(){
        Random random = new Random();
        return terribleTweets[random.nextInt(terribleTweets.length)];
    }





}
