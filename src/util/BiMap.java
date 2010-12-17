/*
 * Bidirectional map
 * This class is not thread safe!
 */

package util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author tnishida
 */
public class BiMap<K,V> implements Map<K,V> {
  private Map<K,V> map;
  private Map<V,K> inverseMap;
  
  public BiMap(){
    map = new HashMap<K,V>();
    inverseMap = new HashMap<V,K>();
  }

  public int size(){
    return map.size();
  }

  public boolean isEmpty(){
    return map.isEmpty();
  }

  public boolean containsKey(Object key){
    return map.containsKey(key);
  }

  public boolean containsValue(Object value){
    return inverseMap.containsKey(value);
  }

  public V get(Object key){
    return map.get(key);
  }

  public V put(K key, V value){
    V old = map.put(key, value);
    inverseMap.put(value, key);
    return old;
  }

  public V remove(Object key){
    V value = map.remove(key);
    inverseMap.remove(value);
    return value;
  }

  public void putAll(Map<? extends K, ? extends V> m){
    for(K key : m.keySet()){
      this.put(key, m.get(key));
    }
  }

  public void clear(){
    map.clear();
    inverseMap.clear();
  }

  public Set<K> keySet(){
    return map.keySet();
  }

  public Collection<V> values(){
    return inverseMap.keySet();
  }

  public Set<Entry<K, V>> entrySet(){
    return map.entrySet();
  }

  public Map<V,K> inverse(){
    return Collections.unmodifiableMap(inverseMap);
  }
  
  public K removeValue(V value){
    K key = inverseMap.remove(value);
    map.remove(key);
    return key;
  }
}
