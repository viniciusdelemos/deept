/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package twitter4j.http;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterTestUnit;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class OAuthTest extends TwitterTestUnit {
    private String consumerSecret;
    private String consumerKey;
    private HttpClient httpClient;

    public OAuthTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        consumerSecret = p.getProperty("consumerSecret");
        consumerKey = p.getProperty("consumerKey");
        httpClient = new HttpClient();
        httpClient.setOAuthConsumer(consumerKey,consumerSecret);

        twitterAPI1 = new Twitter();
        twitterAPI1.setOAuthConsumer(consumerKey, consumerSecret);
        String id1token = p.getProperty("id1.oauth_token");
        String id1tokenSecret = p.getProperty("id1.oauth_token_secret");
        twitterAPI1.setOAuthAccessToken(new AccessToken(id1token,id1tokenSecret));
        twitterAPI1.setRetryCount(3);
        twitterAPI1.setRetryIntervalSecs(10);

        twitterAPI2 = new Twitter();
        twitterAPI2.setOAuthConsumer(consumerKey, consumerSecret);
        String id2token = p.getProperty("id2.oauth_token");
        String id2tokenSecret = p.getProperty("id2.oauth_token_secret");
        twitterAPI2.setOAuthAccessToken(new AccessToken(id2token,id2tokenSecret));

         twitterAPI2.setRetryCount(3);
        twitterAPI2.setRetryIntervalSecs(10);
        unauthenticated = new Twitter();
    }
    protected void tearDown() throws Exception{
        super.tearDown();

    }

    public void testGetToken() throws Exception{
        RequestToken rt = httpClient.getOAuthRequestToken();
        try {
            rt.getAccessToken();
            fail();
        } catch (TwitterException te) {
            te.printStackTrace();
            assertEquals(401, te.getStatusCode());
        }
        Twitter twitter = new Twitter();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        rt = twitter.getOAuthRequestToken();
        try {
            twitter.getOAuthAccessToken(rt.getToken(),rt.getTokenSecret());
            fail();
        } catch (TwitterException te) {
            te.printStackTrace();
            assertEquals(401, te.getStatusCode());
        }
    }

    public void testSign() throws Exception {
        String baseStr = "GET&http%3A%2F%2Fphotos.example.net%2Fphotos&file%3Dvacation.jpg%26oauth_consumer_key%3Ddpf43f3p2l4k3l03%26oauth_nonce%3Dkllo9940pd9333jh%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1191242096%26oauth_token%3Dnnch734d00sl2jdk%26oauth_version%3D1.0%26size%3Doriginal";
        OAuth oauth = new OAuth("dpf43f3p2l4k3l03","kd94hf93k423kf44");
        //http://wiki.oauth.net/TestCases
        assertEquals("tR3+Ty81lMeYAr/Fid0kMTYa/WM=", oauth.generateSignature(baseStr,new RequestToken("nnch734d00sl2jdk","pfkkdhi9sl3r4s00")));
        assertEquals("egQqG5AJep5sJ7anhXju1unge2I=",new OAuth(consumerKey,"cs").generateSignature("bs",new RequestToken("nnch734d00sl2jdk","")));
        assertEquals("VZVjXceV7JgPq/dOTnNmEfO0Fv8=",new OAuth(consumerKey,"cs").generateSignature("bs",new RequestToken("nnch734d00sl2jdk","ts")));
        assertEquals("tR3+Ty81lMeYAr/Fid0kMTYa/WM=",new OAuth(consumerKey,"kd94hf93k423kf44").generateSignature("GET&http%3A%2F%2Fphotos.example.net%2Fphotos&file%3Dvacation.jpg%26oauth_consumer_key%3Ddpf43f3p2l4k3l03%26oauth_nonce%3Dkllo9940pd9333jh%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1191242096%26oauth_token%3Dnnch734d00sl2jdk%26oauth_version%3D1.0%26size%3Doriginal",new RequestToken("nnch734d00sl2jdk","pfkkdhi9sl3r4s00")));
    }
    public void testHeader() throws Exception{
        PostParameter[] params = new PostParameter[2];
        params[0] = new PostParameter("file","vacation.jpg");
        params[1] = new PostParameter("size","original");
        OAuth oauth = new OAuth("dpf43f3p2l4k3l03","kd94hf93k423kf44");
        String expected = "OAuth oauth_consumer_key=\"dpf43f3p2l4k3l03\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1191242096\",oauth_nonce=\"kllo9940pd9333jh\",oauth_version=\"1.0\",oauth_token=\"nnch734d00sl2jdk\",oauth_signature=\"tR3%2BTy81lMeYAr%2FFid0kMTYa%2FWM%3D\"";
        assertEquals(expected,oauth.generateAuthorizationHeader("GET","http://photos.example.net/photos",params,"kllo9940pd9333jh","1191242096",new RequestToken("nnch734d00sl2jdk","pfkkdhi9sl3r4s00")));

    }
    public void testEncodeParameter() throws Exception{
        //http://wiki.oauth.net/TestCases
        assertEquals("abcABC123", OAuth.encode("abcABC123"));
        assertEquals("-._~", OAuth.encode("-._~"));
        assertEquals("%25", OAuth.encode("%"));
        assertEquals("%2B", OAuth.encode("+"));
        assertEquals("%26%3D%2A", OAuth.encode("&=*"));
        assertEquals("%0A", OAuth.encode("\n"));
        assertEquals("%20", OAuth.encode("\u0020"));
        assertEquals("%7F", OAuth.encode("\u007F"));
        assertEquals("%C2%80", OAuth.encode("\u0080"));
        assertEquals("%E3%80%81", OAuth.encode("\u3001"));

        String unreserved = "abcdefghijklmnopqrstuvwzyxABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~";
        assertEquals(unreserved, OAuth.encode(unreserved));

    }
    public void testNormalizeRequestParameters() throws Exception{
        // a=1, c=hi%20there, f=25, f=50, f=a, z=p, z=t
        PostParameter[] params = new PostParameter[]{
                new PostParameter("a","1"),
                new PostParameter("c","hi there"),
                new PostParameter("f","50"),
                new PostParameter("f","25"),
                new PostParameter("z","t"),
                new PostParameter("z","p"),
                new PostParameter("f","a"),
        };
        assertEquals("a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t", OAuth.normalizeRequestParameters(params));

        // test cases from http://wiki.oauth.net/TestCases - Normalize Request Parameters (section 9.1.1)
        params = new PostParameter[]{
                new PostParameter("name",""),
        };
        assertEquals("name=", OAuth.normalizeRequestParameters(params));


        params = new PostParameter[]{
                new PostParameter("a","b"),
        };
        assertEquals("a=b", OAuth.normalizeRequestParameters(params));

        params = new PostParameter[]{
                new PostParameter("a","b"),
                new PostParameter("c","d"),
        };
        assertEquals("a=b&c=d", OAuth.normalizeRequestParameters(params));

        params = new PostParameter[]{
                new PostParameter("a","x!y"),
                new PostParameter("a","x y"),
        };
        assertEquals("a=x%20y&a=x%21y", OAuth.normalizeRequestParameters(params));

        params = new PostParameter[]{
                new PostParameter("x!y","a"),
                new PostParameter("x","a"),
        };
        assertEquals("x=a&x%21y=a", OAuth.normalizeRequestParameters(params));


    }
    public void testConstructRequestURL() throws Exception{
        //http://oauth.net/core/1.0#rfc.section.9.1.2
        assertEquals("http://example.com/resource", OAuth.constructRequestURL("HTTP://Example.com:80/resource?id=123"));
        assertEquals("http://example.com:8080/resource", OAuth.constructRequestURL("HTTP://Example.com:8080/resource?id=123"));
        assertEquals("http://example.com/resource", OAuth.constructRequestURL("HTTP://Example.com/resource?id=123"));
        assertEquals("https://example.com/resource", OAuth.constructRequestURL("HTTPS://Example.com:443/resource?id=123"));
        assertEquals("https://example.com:8443/resource", OAuth.constructRequestURL("HTTPS://Example.com:8443/resource?id=123"));
        assertEquals("https://example.com/resource", OAuth.constructRequestURL("HTTPS://Example.com/resource?id=123"));
    }
}
