package org.feedworker.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.feedworker.exception.ItasaException;
import org.feedworker.object.ItasaUser;
import org.feedworker.object.Show;
import org.feedworker.object.Subtitle;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author luca
 */
public class Itasa extends AbstractQueryXML{
    private final String API_KEY = "apikey=436e566f3d09b217cf687fa5bad5effc";
    private final String STRING_REPLACE = "StringReplace";
    
    private final String OPERATOR_AND = "&";
    private final String OPERATOR_LIKE = "like";
    
    private final String PARAM_FIELDS = "fields[]=";
    private final String PARAM_SEARCH_FIELD = "search_field[]=";
    private final String PARAM_SEARCH_OP = "search_op=";
    private final String PARAM_LIMIT = "limit=";
    private final String PARAM_ORDERBY = "order_by";
    private final String PARAM_SHOW_ID = "show_id=";
    private final String PARAM_VALUE = "value=";
    private final String PARAM_AUTHCODE = "authcode=";
    private final String PARAM_USERNAME = "username=";
    private final String PARAM_PASSWORD = "password=";
    
    private final String STATUS_SUCCESS = "success";
    private final String STATUS_FAIL = "fail";
    
    private final String TAG_STATUS = "status";
    private final String TAG_ERROR = "error";
    private final String TAG_ERROR_MESSAGE = "message";
    private final String TAG_COUNT = "count";
    private final String TAG_USER_AUTHCODE = "authcode";
    private final String TAG_USER_ID = "id";
    private final String TAG_USER_HASMYITASA = "has_myitasa";
    private final String TAG_SHOW_PLOT = "plot";
    private final String TAG_SHOW_GENRES = "genres";
    private final String TAG_SHOW_BANNER = "banner";
    private final String TAG_SHOW_STARTED = "started";
    private final String TAG_SHOW_ENDED = "ended";
    private final String TAG_SHOW_COUNTRY = "country";
    private final String TAG_SHOW_SEASONS = "seasons";
    private final String TAG_SHOW_NETWORK = "network";
    private final String TAG_SHOW_NAME = "name";
    private final String TAG_SHOW_ID = "id";
    private final String TAG_SHOW_ACTORS = "actors";
    private final String TAG_SHOW_ACTOR_NAME = "name";
    private final String TAG_SHOW_ACTOR_AS = "as";
    private final String TAG_SUBTITLE_ID = "id";
    private final String TAG_SUBTITLE_NAME = "name";
    private final String TAG_SUBTITLE_VERSION = "version";
    private final String TAG_SUBTITLE_FILENAME = "filename";
    private final String TAG_SUBTITLE_FILESIZE = "filesize";
    private final String TAG_SUBTITLE_DESCRIPTION = "description";
    private final String TAG_SUBTITLE_SUBMIT_DATE = "submit_date";
    
    private final String URL_BASE = "http://api.italiansubs.net/api/rest";
    private final String URL_LOGIN = URL_BASE + "/users/login/?"; //OK
    private final String URL_MYITASA_SHOWS = URL_BASE + "/myitasa/shows?";
    private final String URL_SHOW_SINGLE = 
                                    URL_BASE + "/shows/" + STRING_REPLACE + "?"; //OK
    private final String URL_SHOW_LIST = URL_BASE + "/shows/?"; //OK
    private final String URL_SUBTITILE_SINGLE = 
                                URL_BASE + "/subtitles/" + STRING_REPLACE + "?"; //OK
    private final String URL_SUBTITILE_SHOW = URL_BASE + "/subtitle/subtitles/?";
    private final String URL_SUBTITILE_SEARCH = URL_BASE + "/subtitle/search/?";
    
    private String status, error;
    
    public Show showSingle(String id, boolean flag_actors) throws JDOMException, 
                                                    IOException, ItasaException{
        buildUrl(composeUrl(URL_SHOW_SINGLE.replaceFirst(STRING_REPLACE, id), null));
        checkStatus();
        Show show = null;
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            Element item = (Element) iter.next();
            String plot = item.getChild(TAG_SHOW_PLOT).getText();
            String banner = item.getChild(TAG_SHOW_BANNER).getText();
            String season  = item.getChild(TAG_SHOW_SEASONS).getText();
            String started  = item.getChild(TAG_SHOW_STARTED).getText();
            String ended  = item.getChild(TAG_SHOW_ENDED).getText();
            String country = item.getChild(TAG_SHOW_COUNTRY).getText();
            String network = item.getChild(TAG_SHOW_NETWORK).getText();
            ArrayList genres = new ArrayList();
            Iterator it = getIteratorGenres(item);
            while (it.hasNext()){
                genres.add(((Element) it.next()).getText());
            }
            ArrayList<String[]> actors = null;
            if (flag_actors){
                actors = new ArrayList<String[]>();
                it = ((Element)item.getChildren(TAG_SHOW_ACTORS).get(0))
                                .getChildren().iterator();
                while (it.hasNext()){
                    Element temp = (Element) it.next();
                    actors.add(new String[]{temp.getChildText(TAG_SHOW_ACTOR_NAME),
                                            temp.getChildText(TAG_SHOW_ACTOR_AS)});
                }
            }
            show = new Show(plot, banner, season, country, network, started,
                                ended, genres, actors);
        } else
            throw new ItasaException("show single: "+ error);
        return show;
    }

    public TreeMap<String, String> showList() throws JDOMException, IOException, 
                                                        ItasaException {
        TreeMap<String, String> container = null;
        buildUrl(composeUrl(URL_SHOW_LIST, null));
        checkStatus();
        if (isStatusSuccess()){
            container = new TreeMap<String, String>();
            Iterator iter =  getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String id = item.getChild(TAG_SHOW_ID).getText();
                String name = item.getChild(TAG_SHOW_NAME).getText();
                container.put(name, id);
            }
        } else 
            throw new ItasaException("mapIdName: "+ error);
        return container;
    }

    public Subtitle subtitleSingle(String id) throws JDOMException, IOException, 
                                                                    ItasaException{
        buildUrl(composeUrl(URL_SUBTITILE_SINGLE.replaceFirst(STRING_REPLACE, id), null));
        checkStatus();
        Subtitle sub = null;
        if (isStatusSuccess()){
            Iterator iter = ((Element) document.getRootElement().getChildren().get(0))
                .getChildren().iterator();
            Element item = (Element) iter.next();
            String name = item.getChild(TAG_SUBTITLE_NAME).getText();
            String version = item.getChild(TAG_SUBTITLE_VERSION).getText();
            String filename  = item.getChild(TAG_SUBTITLE_FILENAME).getText();
            String filesize  = item.getChild(TAG_SUBTITLE_FILESIZE).getText();
            String date = item.getChild(TAG_SUBTITLE_SUBMIT_DATE).getText();
            String description  = item.getChild(TAG_SUBTITLE_DESCRIPTION).getText();
            sub = new Subtitle(name, version, filename, filesize, date, description);
        } else
            throw new ItasaException("subtitleSingle: "+ error);
        return sub;
    }
    
    public ArrayList<Subtitle> subtitleListByIdShow(int idShow) throws JDOMException, 
                                                        IOException, ItasaException{
        ArrayList params = new ArrayList();
        params.add(PARAM_SHOW_ID + idShow);
        params.add(PARAM_FIELDS + TAG_SUBTITLE_ID);
        params.add(PARAM_FIELDS + TAG_SUBTITLE_NAME);
        params.add(PARAM_FIELDS + TAG_SUBTITLE_VERSION);
        buildUrl(composeUrl(URL_SUBTITILE_SHOW, params));
        checkStatus();
        ArrayList<Subtitle>  subs = null;
        if (isStatusSuccess()){
            subs = new ArrayList<Subtitle>();
            Iterator iter = getDescendantsZero(2);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String id = item.getChild(TAG_SUBTITLE_ID).getText();
                String name = item.getChild(TAG_SUBTITLE_NAME).getText();
                String version = item.getChild(TAG_SUBTITLE_VERSION).getText();
                Subtitle sub = new Subtitle(id, name, version);
                subs.add(sub);
            }
        } else
            throw new ItasaException("subtitleListByIdShow: "+ error);
        return subs;
    }
    
    public ArrayList<Subtitle> searchSubtitleCompleted(String id, String order, 
                                                    String limit) throws JDOMException, 
                                                    IOException, ItasaException{
        //[name]=asc
        ArrayList params = new ArrayList();
        if (id!=null)
            params.add(PARAM_SHOW_ID + id);
        params.add(PARAM_VALUE + "%completa%25");
        params.add(PARAM_SEARCH_FIELD + TAG_SUBTITLE_NAME);
        params.add(PARAM_SEARCH_OP + OPERATOR_LIKE);
        if (order!=null)
            params.add(PARAM_ORDERBY+order);
        if (limit!=null)
            params.add(PARAM_LIMIT + limit);
        buildUrl(composeUrl(URL_SUBTITILE_SEARCH, params));
        checkStatus();
        ArrayList<Subtitle>  subs = null;
        if (isStatusSuccess()){
            subs = new ArrayList<Subtitle>();
            Iterator iter = getDescendantsZero(1);
            while (iter.hasNext()){
                Element item = (Element) iter.next();
                String idSub = item.getChild(TAG_SUBTITLE_ID).getText();
                String name = item.getChild(TAG_SUBTITLE_NAME).getText();
                String version = item.getChild(TAG_SUBTITLE_VERSION).getText();
                Subtitle sub = new Subtitle(idSub, name, version);
                subs.add(sub);
            }
        } else
            throw new ItasaException("searchSubtitleCompleted: "+ error);
        return subs;
    }
    
    public ItasaUser login(String user, String pwd) throws JDOMException, IOException, 
                                                                    ItasaException{
        ArrayList params = new ArrayList();
        params.add(PARAM_USERNAME + user);
        params.add(PARAM_PASSWORD + pwd);
        buildUrl(composeUrl(URL_LOGIN, params));
        checkStatus();
        ItasaUser itasa = null;
        if (isStatusSuccess()){
            Element item = (Element) getDescendantsZero(1).next();
            String id = item.getChild(TAG_USER_ID).getText();
            String authcode = item.getChild(TAG_USER_AUTHCODE).getText();
            String hasmyitasa = item.getChild(TAG_USER_HASMYITASA).getText();
            boolean myitasa = false;
            if (hasmyitasa.equals("1"))
                myitasa = true;
            itasa = new ItasaUser(authcode, id, myitasa);
        } else
            throw new ItasaException(error);
        return itasa;
    }
    
    public void myItasaShows(String authcode) throws JDOMException, IOException{
        ArrayList params = new ArrayList();
        params.add(PARAM_AUTHCODE + authcode);
        buildUrl(composeUrl(URL_MYITASA_SHOWS, params));
    }
    
    /**Compone la url compresa di parametri
     * 
     * @param url url base
     * @param params parametri da aggiungere per la query
     * @return 
     */
    private String composeUrl(final String url, ArrayList params){
        String newUrl = url + API_KEY;
        if (params!=null)
            for (int i=0; i<params.size(); i++)
                newUrl+= OPERATOR_AND + params.get(i).toString();
        System.out.println(newUrl);
        return newUrl;
    }
    
    private Iterator getIteratorGenres(Element item){
        return item.getChild(TAG_SHOW_GENRES).getChildren().iterator();
    }
    
    /**controlla lo status presente nel document
     * se falso preleva/imposta il msg d'error
     */
    private void checkStatus(){
        Iterator iterator = document.getContent().iterator();
        while (iterator.hasNext()) {
            Element item = (Element) iterator.next();
            status = item.getChild(TAG_STATUS).getText();
            if (isStatusFail())
                error = ((Element)item.getChildren(TAG_ERROR).get(0)).
                                        getChild(TAG_ERROR_MESSAGE).getText();
        }
    }
    
    private int getResponseCount(){
        Element item = (Element) getDescendantsZero(0).next();
        return Integer.parseInt(item.getChild(TAG_COUNT).getText());
    }
    
    /**Controlla se lo status è positivo
     * 
     * @return
     */
    private boolean isStatusSuccess(){
        return status.equals(STATUS_SUCCESS);
    }
    
    /**Controlla se lo status è fail
     * 
     * @return 
     */
    private boolean isStatusFail(){
        return status.equals(STATUS_FAIL);
    }
    
    public static void main (String[] args){
        Itasa i = new Itasa();
        try {
            ItasaUser iu = i.login("judge", "qwerty");
            i.myItasaShows(iu.getAuthcode());
            /*
            Show s = i.showSingle("1363", true);
            Subtitle sub = i.subtitleSingle("20000");
            Object[] obj = sub.toArraySingle();
            for (int n=0; n<obj.length; n++)
                System.out.println(obj[n]);
            
            ArrayList<Subtitle> a = i.subtitleListByIdShow(1363);
            for (int m=0; m<a.size(); m++){
            Object[] obj = a.get(m).toArrayIdNameVersion();
            for (int n=0; n<obj.length; n++)
            System.out.println(obj[n]);
            }
             */
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ItasaException ex) {
            System.out.println(ex.getMessage());
        }
    }
}