package yourPackageName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by amirkhorsandi on 3/3/17.
 * https://github.com/amirdew/JSON
 */

//////////////////////////////////////////USAGE//////////////////////////////////////////
//        //generate json string:
//        JSON generatedJsonObject = JSON.create(
//                JSON.dic(
//                        "someKey", "someValue",
//                        "someArrayKey", JSON.array(
//                                "first",
//                                1,
//                                2,
//                                JSON.dic(
//                                        "emptyArrayKey", JSON.array()
//                                )
//                        )
//                )
//        );
//        d.d(TAG + "DEBUG_LOG", generatedJsonObject.toString());
//////////////////////////////////////////USAGE//////////////////////////////////////////


@SuppressWarnings({"WeakerAccess", "unused", "NullableProblems"})
public class JSON {

    private JSONObject jsObj;
    private JSONArray jsArr;
    private Object value;


    public JSON(Object jsonString) {
        if (jsonString instanceof ArrayList)
            jsonString = new JSONArray((ArrayList) jsonString);
        if (null == jsonString) {
            value = null;
        } else if (jsonString.toString().trim().startsWith("{")) {
            //json object
            try {
                jsObj = new JSONObject(jsonString.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (jsonString.toString().trim().startsWith("[")) {
            //json array
            try {
                jsArr = new JSONArray(jsonString.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            value = jsonString;
    }


    @Override
    public String toString() {
        try {
            if (isNull())
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return this.value().toString();
        }catch (Throwable tr){
            return "";
        }
    }

    public int count() {
        if (null != jsObj) return jsObj.length();
        if (null != jsArr) return jsArr.length();
        return 0;
    }

    public JSON key(String keyStr) {
        if (null == jsObj) return new JSON(null);
        try {
            Object obj = jsObj.get(keyStr);
            //noinspection ConstantConditions
            if (null != obj)
                return new JSON(obj);
        } catch (JSONException ignored) { }
        return new JSON(null);
    }


    public JSON index(int index) {
        if (null == jsArr) return new JSON(null);
        try {
            return new JSON(jsArr.get(index));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSON(null);
    }


    public String stringValue() {
        if (null == this.value())
            return "";
        try {
            if (isNull())
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(this.value());
    }

    public int intValue() {
        if (null == this.value()) return 0;
        try {
            return Integer.valueOf(this.value().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public long longValue() {
        if (null == this.value()) return 0;
        try {
            return Long.valueOf(this.value().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public Double doubleValue() {
        if (null == this.value()) return 0.0;
        try {
            return Double.valueOf(this.value().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public boolean booleanValue() {
        if (null == this.value()) return false;
        try {
            return "true".equals(this.stringValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Object value() {
        if (null != nullableValue()) return nullableValue();
        return new JSON(null);
    }


    public boolean isNull() {
        if (null == value && null == jsArr && null == jsObj) {
            return true;
        }
        return "null".equals(String.valueOf(this.value()));
    }

    private Object nullableValue() {
        if (null != value) return value;
        if (null != jsObj) return jsObj.toString();
        if (null != jsArr) return jsArr.toString();
        return null;
    }


    public boolean exist() {
        return null != nullableValue();
    }


    public JSONObject getJsonObject() {
        return jsObj;
    }

    public JSONArray getJsonArray() {
        return jsArr;
    }

    public void removeWithKey(String Key) {
        if (null == Key)
            return;
        JSONObject jsonObject = this.getJsonObject();
        if (null == jsonObject)
            return;
        jsonObject.remove(Key);
        this.jsObj = jsonObject;
    }

    public void addEditWithKey(String Key, Object Object) {
        if (null == Object || null == Key)
            return;
        JSONObject jsonObject = this.getJsonObject();
        if (null == jsonObject)
            return;
        Object normalizedObject = Object;
        if (Object instanceof JSON) {
            if (null != ((JSON) Object).getJsonArray())
                normalizedObject = ((JSON) Object).getJsonArray();
            else if (null != ((JSON) Object).getJsonObject())
                normalizedObject = ((JSON) Object).getJsonObject();
            else
                normalizedObject = ((JSON) Object).value();
        }
        try {
            jsonObject.putOpt(Key, normalizedObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.jsObj = jsonObject;
    }


    public void addWithIndex(Object inputObject, int index) {
        if (null == inputObject)
            return;
        JSONArray jsonArr = this.getJsonArray();
        if (null == jsonArr)
            return;
        try {
            if (inputObject instanceof JSON) {
                if (null != ((JSON) inputObject).getJsonArray()) {
                    if (index == -1)
                        jsonArr.put(((JSON) inputObject).getJsonArray());
                    else
                        jsonArr.put(index, ((JSON) inputObject).getJsonArray());
                } else if (null != ((JSON) inputObject).getJsonObject()) {
                    if (index == -1)
                        jsonArr.put(((JSON) inputObject).getJsonObject());
                    else
                        jsonArr.put(index, ((JSON) inputObject).getJsonObject());
                } else {
                    if (index == -1)
                        jsonArr.put(((JSON) inputObject).value());
                    else
                        jsonArr.put(index, ((JSON) inputObject).value());
                }
            } else {
                if (index == -1)
                    jsonArr.put(inputObject);
                else if (index > -1)
                    jsonArr.put(index, inputObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.jsArr = jsonArr;
    }


    public void add(Object inputObject) {
        addWithIndex(inputObject, -1);
    }


    public void removeWithIndex(int index) {
        JSONArray jsonArr = this.getJsonArray();
        if (null == jsonArr)
            return;
        JSONArray newJsonArr = new JSONArray();
        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                if (i != index) {
                    newJsonArr.put(jsonArr.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.jsArr = newJsonArr;
    }


    public void remove(Object inputObject) {
        if (null == inputObject)
            return;
        JSONArray jsonArr = this.getJsonArray();
        if (null == jsonArr)
            return;
        JSONArray newJsonArr = new JSONArray();
        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                if (inputObject instanceof JSONObject && jsonArr.get(i) instanceof JSONObject) {
                    if (!jsonObjectComparesEqual((JSONObject) jsonArr.get(i), (JSONObject) inputObject, null, null)) {
                        newJsonArr.put(jsonArr.get(i));
                    }
                } else {
                    if (!jsonArr.get(i).equals(inputObject)) {
                        newJsonArr.put(jsonArr.get(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.jsArr = newJsonArr;
    }


    public static JSON create(Object object) {
        return new JSON(object.toString());
    }


    public static JSONObject dic(Object... values) {
        JSONObject mainDic = new JSONObject();
        for (int i = 0; i < values.length; i += 2) {
            try {
                Object valueObject = values[i + 1];
                if (valueObject instanceof JSON) {
                    if (null != ((JSON) valueObject).getJsonArray())
                        valueObject = ((JSON) valueObject).getJsonArray();
                    else if (null != ((JSON) valueObject).getJsonObject())
                        valueObject = ((JSON) valueObject).getJsonObject();
                }
                mainDic.put((String) values[i], null == valueObject ? JSONObject.NULL : valueObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mainDic;
    }


    public static JSONArray array(Object... values) {
        JSONArray mainList = new JSONArray();
        for (Object obj : values) {
            mainList.put(obj);
        }
        return mainList;
    }


    public static boolean jsonObjectComparesEqual(JSONObject x, JSONObject y, Collection<String> only, Collection<String> except) {
        Set<String> keys = keySet(x);
        keys.addAll(keySet(y));
        if (null != only) {
            keys.retainAll(only);
        }
        if (except != null) {
            keys.removeAll(except);
        }
        for (String s : keys) {
            Object a = null, b = null;
            try {
                a = x.get(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                b = x.get(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (null != a) {
                if (!a.equals(b)) {
                    return false;
                }
            } else if (null != b) {
                //noinspection ConstantConditions
                if (!b.equals(a)) {
                    return false;
                }
            }
        }
        return true;
    }


    @SuppressWarnings("Convert2Diamond")
    private static Set<String> keySet(JSONObject j) {
        Set<String> res = new TreeSet<String>();
        for (String s : new AsIterable<String>(j.keys())) {
            res.add(s);
        }
        return res;
    }


    private static class AsIterable<T> implements Iterable<T> {
        private Iterator<T> iterator;

        public AsIterable(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        public Iterator<T> iterator() {
            return iterator;
        }
    }


}



/*****************************
 USAGE::::::::::::::::USAGE::::::::::::::::::::::USAGE::::::::::::::::::::::USAGE

 Usage - parse json

 You can create JSON object from string and access data with key() and index() methods.

 String simpleJsonString = "{\"id\":1,\"name\":\"A green door\",\"price\":12.5,\"tags\":[\"home\",\"green\"]}";
 JSON json = new JSON(simpleJsonString);
 //access data
 String firstTag = json.key("tags").index(0).stringValue();
 Double price = json.key("price").doubleValue();


 products json:

 [
 {
 "id": 2,
 "name": "An ice sculpture",
 "price": 12.50,
 "tags": ["cold", "ice"],
 "dimensions": {
 "length": 7.0,
 "width": 12.0,
 "height": 9.5
 },
 "warehouseLocation": {
 "latitude": -78.75,
 "longitude": 20.4
 }
 },
 {
 "id": 3,
 "name": "A blue mouse",
 "price": 25.50,
 "dimensions": {
 "length": 3.1,
 "width": 1.0,
 "height": 1.0
 },
 "warehouseLocation": {
 "latitude": 54.4,
 "longitude": -32.7
 }
 }
 ]






 loop:

 for(int i=0; i<products.count(); i++){
 json productInfo = products.index(i);
 String productName = productInfo.key("name").stringValue();
 }






 JSON is exception and null free, you can use key() and index() many times without worry about any exception.

 int someValue = products.index(8).key("someKey").index(1).key("someOther").intValue();
 //someValue = 0







 check index or key is exist or is null:


 if( products.index(3).key("someKey").isNull() ){
 //
 }

 if( products.index(1).key("someKey").exist() ){
 //
 }





 Available methods - parse

 Method	Input type	Return type	Default	Description
 key()	String	JSON	-	if object is a dictionary return JSON object with input key
 index()	int	JSON	-	if object is a array return JSON object with input index
 stringValue()	-	String	empty string ("")	return .toString() of object
 intValue()	-	int	0	return integer value if possible
 longValue()	-	long	0	return long value if possible
 doubleValue()	-	Double	0	return Double value if possible
 booleanValue()	-	boolean	false	return true if object is kind of boolean and true or is kind of string and equal "true"
 value()	-	Object	-	return related object
 count()	-	int	0	if related object is a dictionary return number of keys, if related object is a array return length of array
 isNull()	-	boolean	-	return true if related object is null
 exist()	-	boolean	-	return true if related object with index or key exists
 getJsonArray()	-	JSONArray	null	return related JSONArray
 getJsonObject()	-	JSONObject	null	return related JSONObject










 Usage - generate json, data holding
 You can use JSON.dic() and JSON.array() static methods to generate json string or hold and pass data


 JSON generatedJsonObject = JSON.create(
 JSON.dic(
 "someKey", "someValue",
 "someArrayKey", JSON.array(
 "first",
 1,
 2,
 JSON.dic(
 "emptyArrayKey", JSON.array()
 )
 )
 )
 );

 String jsonString = generatedJsonObject.toString();

 result:

 {
 "someKey": "someValue",
 "someArrayKey": [
 "first",
 1,
 2,
 {
 "emptyArrayKey": []
 }
 ]
 }








 add, edit, remove:
 note: now its work for first level only

 generatedJsonObject.addEditWithKey("someArrayKey","someOtherValue");




 result:

 {
 "someKey": "someValue",
 "someArrayKey": "someOtherValue"
 }






 Available methods - generate, edit, remove


 Method	Input type	Description
 add()	Object	if current related object is an array add object at end of array
 remove()	Object	if current related object is an array and input object exist, remove it from array
 addWithIndex()	Object, int	if current related object is an array replace input object with object for input index
 removeWithIndex()	int	if current related object is an array remove object for input index
 addEditWithKey()	String, Object	if current related object is a dictionary add input object with input key
 removeWithKey()	String	if current related object is a dictionary remove object with input key
 static create()	Object	create new instance of JSON with input object
 static dic()	Object...	if number of input objects is even create JSONObject use even objects as key and odd obejcts as value
 static array()	Object...	create JSONArray use input objects



 */








