package com.denspark.strelets.cinematrix.not_for_production;

import android.text.TextUtils;

import com.github.code.Itertools;
import com.migcomponents.migbase64.Base64;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilmixDecoder {
    private static final String TAG = "FilmixDecoder";
    org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();


    public static void main(String[] args) {
        FilmixDecoder decoder = new FilmixDecoder();
        String s_1 = "NW4dzGVfQcEXOCRnamRnyGAJO7AfzM6nB7zRy7akgiseac4jBjwUOGLdaHBdgjohOGlUa7AMam1VgF6cyC1wND1MQouhgbXhzi1=Q7sUy7zpLl4SN5ApLogGQooQwk12QCUIQGLRyCn0Qi9UaArr";

        String s_2 = "#2W3sidGl0bGUiOiIg0KHQtdC30L7QvSAxIiwiZm9sZGVyIjpbeyJ0aXRsZSI6ItCh0LXRgNC40Y8gMSAo0KHQtdC30L7QvSAxKSIsImlkIjoiczFlMSIsImZpbGUiOiJbNDgwcF1odHRwczovL3ZzMC5jZG5sYXN0LmNvbS9zLzJlMzhmMzEyYzlkNGExZWQ0ZjYwZTZlODc1MGZkNDQ2ZDYxYTExL0pldXgtZGluZmx1ZW5jZS0yMDE4LUZyYS9zMDFlMDFfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMC5jZG5sYXN0LmNvbS9zLzJlMzhmMzEyYzlkNGExZWQ0ZjYwZTZlODc1MGZkNDQ2ZDYxYTExL0pldXgtZGluZmx1ZW5jZS0yMDE4LUZyYS9zMDFlMDFfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDIgKNCh0LXQt9C+0L0gMSkiLCJpZCI6InMxZTIiLCJmaWxlIjoiWzQ4MHBdaHR0cHM6Ly92czAuY2RubGFzdC5jb20vcy8yZTM4ZjMxMmM5ZDRhMWVkNGY2MGU2ZTg3NTBmZDQ0NmQ2MWExMS9KZXV4LWRpbmZsdWVuY2UtMjAxOC1GcmEvczAxZTAyXzQ4MC5tcDQsWzcy//c2ljYXJpby4yMi5tb3ZpZXM=MHBdaHR0cHM6Ly92czAuY2RubGFzdC5jb20vcy8yZTM4ZjMxMmM5ZDRhMWVkNGY2MGU2ZTg3NTBmZDQ0NmQ2MWExMS9KZXV4LWRpbmZsdWVuY2UtMjAxOC1GcmEvczAxZTAyXzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyAzICjQodC10LfQvtC9IDEpIiwiaWQiOiJzMWUzIiwiZmlsZSI6Ils0ODBwXWh0dHBzOi8vdnMwLmNkbmxhc3QuY29tL3MvMmUzOGYzMTJjOWQ0YTFlZDRmNjBlNmU4NzUwZmQ0NDZkNjFhMTEvSmV1eC1kaW5mbHVlbmNlLTIwMTgtRnJhL3MwMWUwM180ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMwLmNkbmxhc3QuY29tL3MvMmUzOGYzMTJjOW//a2lub2NvdmVyLnc5OC5uamJoQ0YTFlZDRmNjBlNmU4NzUwZmQ0NDZkNjFhMTEvSmV1eC1kaW5mbHVlbmNlLTIwMTgtRnJhL3MwMWUwM183MjAubXA0In0seyJ0aXRsZSI6ItCh0LXRgNC40Y8gNCAo0KHQtdC30L7QvSAxKSIsImlkIjoiczFlNCIsImZpbGUiOiJbNDgwcF1odHRwczovL3ZzMi5jZG5sYXN0LmNvbS9zLzJlMzhmMzEyYzlkNGExZWQ0ZjYwZTZlODc1MGZkNDQ2ZDYxYTExL0pldXgtZGluZmx1ZW5jZS0yMDE4LUZyYS9zMDFlMDRfNDgwLm1wNCxbNzIwcF1odHRwczovL3ZzMi5jZG5sYXN0LmNvbS9zLzJlMzhmMzEyYzlkNGExZWQ0ZjYwZTZlODc1MGZkNDQ2ZDYxYTExL0pldXgtZGluZmx1ZW5jZS0yMDE4LUZyYS9zMDFlMD//Y2VyY2EudHJvdmEuc2FnZ2V6emE=RfNzIwLm1wNCJ9LHsidGl0bGUiOiLQodC10YDQuNGPIDUgKNCh0LXQt9C+0L0gMSkiLCJpZCI6InMxZTUiLCJmaWxlIjoiWzQ4MHBdaHR0cHM6Ly92czIuY2RubGFzdC5jb20vcy8yZTM4ZjMxMmM5ZDRhMWVkNGY2MGU2ZTg3NTBmZDQ0NmQ2MWExMS9KZXV4LWRpbmZsdWVuY2UtMjAxOC1GcmEvczAxZTA1XzQ4MC5tcDQsWzcyMHBdaHR0cHM6Ly92czIuY2RubGFzdC5jb20vcy8yZTM4ZjMxMmM5ZDRhMWVkNGY2MGU2ZTg3NTBmZDQ0NmQ2MWExMS9KZXV4LWRpbmZsdWVuY2UtMjAxOC1GcmEvczAxZTA1XzcyMC5tcDQifSx7InRpdGxlIjoi0KHQtdGA0LjRjyA2ICjQodC10LfQvtC9IDEpIiwiaWQiOiJzMWU2IiwiZmlsZSI6Ils0ODBwXWh0dHBzOi8vdnMyLmNkbmxhc3QuY29tL3MvMmUzOGYzMTJjOWQ0YTFlZDRmNjBlNmU4NzUwZmQ0NDZkNjFhMTEvSmV1eC1kaW5mbHVlbmNlLTIwMTgtRnJhL3MwMWUwNl80ODAubXA0LFs3MjBwXWh0dHBzOi8vdnMyLmNkbmxhc3QuY29tL3MvMmUzOGYzMTJjOWQ0YTFlZDRmNjBlNmU4NzUwZmQ0NDZkNjFhMTEvSmV1eC1kaW5mbHVlbmNlLTIwMTgtRnJhL3MwMWUwNl83MjAubXA0In1dfV0=";
        String s_3 = "#06807407407003a02f02f07303702e07407606206507307402e06e06507402f06606906c06d02f06602f03203003103702f06606f07207306506706505f03802e06d070034";
//
//        System.out.println(urlDecodeBase64(s_1));
//        System.out.println(urlDecodeBase64_v2(s_2));
//        System.out.println(urlDecodeUnicode(s_3));


        System.out.println(decoder.decodeMediaUrl(s_3));

    }

    private String urlDecodeBase64(String s) {
        String codec_a = "y5U4ei6d7NJgtG2VlBxfsQ1Hz=";
        String codec_b = "MXwR3m80TauZpDbokYnvIL9Wcr";
        String[] codec_a_arr = codec_a.split("");
        String[] codec_b_arr = codec_b.split("");
        for (int i = 0; i < codec_a_arr.length; i++) {
            s = s.replace(codec_a_arr[i], "*");
            s = s.replace(codec_b_arr[i], codec_a_arr[i]);
            s = s.replace("*", codec_b_arr[i]);
        }
        String decodedUrl = StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.decodeBase64(s));
        return decodedUrl;
    }

    //    def decode_base64_2(self, encoded_url):
//    tokens = ("//Y2VyY2EudHJvdmEuc2FnZ2V6emE=", "//c2ljYXJpby4yMi5tb3ZpZXM=", "//a2lub2NvdmVyLnc5OC5uamJo")
//    clean_encoded_url = encoded_url[2:].replace("\/","/")
//
//        for token in tokens:
//    clean_encoded_url = clean_encoded_url.replace(token, "")
//
//            return base64.b64decode(clean_encoded_url)
    public String urlDecodeBase64_v2(String s) {
        if (s != null) {
            ArrayList<String> tokens = new ArrayList<>();
//                    "//Y2VyY2EudHJvdmEuc2FnZ2V6emE=",
//                    "//c2ljYXJpby4yMi5tb3ZpZXM=",
//                    "//a2lub2NvdmVyLnc5OC5uamJo"
            tokens.add("//UEplcmwyRkcxbUN5MDJMRA==");
            tokens.add("//bVBWUnM4SXZQV0RSeFEybA==");
            tokens.add("//Z0p1bnlRYlNzVWptV3Y1SA==");
            String clean_encoded_url = s.substring(2).replace("\\/", "/");

            ArrayList<String> innerTokens = new ArrayList<>();
            for (String token : tokens) {
                if (clean_encoded_url.contains(token)) {
                    clean_encoded_url = clean_encoded_url.replace(token, "");
                } else {
                    innerTokens.add(token);
                }
            }
            ArrayList<String> errorInTokens = new ArrayList<>();
            if (!innerTokens.isEmpty()) {
                for (String innerToken : innerTokens) {
                    if (clean_encoded_url.contains(innerToken)) {
                        clean_encoded_url = clean_encoded_url.replace(innerToken, "");
                    } else {
                        errorInTokens.add(innerToken);
                    }
                }
            }
            if (!errorInTokens.isEmpty()) {
                System.out.println("error in tokens");
            }

            return StringUtils.newStringUtf8(base64.decode(clean_encoded_url.getBytes()));
        }
        return null;
    }

    public String urlDecodeBase64_v3(String s) {
        if (s != null) {
            String clean_encoded_url = s.substring(2).replace("\\/", "/");

//            List<String> tokens = new ArrayList<String>();

            try {
//                int tokensCount = 0;
                Pattern regexCount = Pattern.compile("[\\W&&[^=]]+");
                Pattern regex = Pattern.compile("((?::<:))\\w{24}");
                Matcher regexMatcherCount = regexCount.matcher(clean_encoded_url);
                while (regexMatcherCount.find()) {


                    Matcher regexInnerMatcher = regex.matcher(clean_encoded_url);

                    while (regexInnerMatcher.find()) {
                        String token = regexInnerMatcher.group();
                        clean_encoded_url = clean_encoded_url.replace(token, "");
//                        tokensCount++;
                    }
                }
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }
            String encodedString = StringUtils.newStringUtf8(Base64.decode(clean_encoded_url));
            return encodedString;
        }
        return null;
    }

    private String urlDecodeUnicode(String s) {
        String encodedUrl;
        if (s.contains("#")) {
            encodedUrl = s.substring(1);
        } else {
            encodedUrl = s;
        }
        StringBuilder unicodeUrl = new StringBuilder();
        for (List<String> stringList : specGrouper3L(encodedUrl)) {
            List<String> tempStringList = new ArrayList<>();
            tempStringList.add("\\u0");
            tempStringList.addAll(stringList);
            unicodeUrl.append(TextUtils.join("", tempStringList));
        }
        String nonUnicodeDecodedUrl = unicodeUrl.toString();

        return decodeUnicode(nonUnicodeDecodedUrl);
    }

    private static String decodeUnicode(String encoded_s) {
        encoded_s = StringEscapeUtils.unescapeJava(encoded_s);
        return encoded_s;
    }

    List<String> makeLinksList(String s) {
        List<String> linksList = new ArrayList<>();
        List<String> listOfEndings = listOfEndings(s);
        for (String quality : listOfEndings) {
            try {
                String resultString = s.replaceAll("\\[.*\\]", quality);
                linksList.add(resultString);
            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            } catch (IllegalArgumentException ex) {
                // Syntax error in the replacement text (unescaped $ signs?)
            } catch (IndexOutOfBoundsException ex) {
                // Non-existent backreference used the replacement text
            }
        }
        return linksList;
    }

    List<String> listOfEndings(String s) {
        String ResultString = null;
        try {
            Pattern regex = Pattern.compile("\\[.*\\]");
            Matcher regexMatcher = regex.matcher(s);
            if (regexMatcher.find()) {
                ResultString = regexMatcher.group();
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        List<String> matchList = new ArrayList<String>();
        try {
            Pattern regex = Pattern.compile("[\\d]+[\\w]*");
            Matcher regexMatcher = regex.matcher(ResultString);
            while (regexMatcher.find()) {
                matchList.add(regexMatcher.group());
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return matchList;
    }

    private Iterable<List<String>> specGrouper3L(String s) {
        ArrayList<String> strings = new ArrayList<>();
        for (char c : s.toCharArray()) {
            strings.add(Character.toString(c));
        }
        Iterator<String> stringIterator = strings.iterator();
        List<String> listOne = new ArrayList<>();
        List<String> listTwo = new ArrayList<>();
        List<String> listThree = new ArrayList<>();
        while (stringIterator.hasNext()) {

            listOne.add(stringIterator.next());

            if (stringIterator.hasNext()) {
                listTwo.add(stringIterator.next());
            }

            if (stringIterator.hasNext()) {
                listThree.add(stringIterator.next());
            }
        }
        return Itertools.izipLongest("x", listOne, listTwo, listThree);
    }

//    def decode_direct_media_url(self, encoded_url, checkhttp=False):
//            if(checkhttp == True and (encoded_url.find('http://') != -1 or encoded_url.find('https://') != -1)):
//            return False
//
//        try:
//                if encoded_url.find('#') != -1:
//                if encoded_url[:2] == '#2':
//            return self.decode_base64_2(encoded_url)
//            else:
//            return self.decode_unicode(encoded_url)
//            else:
//            return self.decode_base64(encoded_url)
//    except:
//            return False

    public String decodeMediaUrl(String encodedUrl) {
        if (encodedUrl.contains("#")) {
            if (encodedUrl.startsWith("#2")) {
                return urlDecodeBase64_v2(encodedUrl);
            } else {
                return urlDecodeUnicode(encodedUrl);
            }
        } else {
            return urlDecodeBase64(encodedUrl);
        }
    }

}
