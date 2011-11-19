package com.sensei.search.req;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.lucene.search.SortField;
import org.json.JSONArray;
import org.json.JSONObject;

import com.browseengine.bobo.api.BrowseSelection;
import com.browseengine.bobo.api.BrowseSelection.ValueOperation;
import com.browseengine.bobo.api.FacetSpec;
import com.browseengine.bobo.facets.DefaultFacetHandlerInitializerParam;
import com.browseengine.bobo.facets.FacetHandlerInitializerParam;

public class SenseiRequest implements AbstractSenseiRequest, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
  /**
   * The transaction ID
   */
  private long tid = -1;

  private HashMap<String,BrowseSelection> _selections;
	private ArrayList<SortField> _sortSpecs;
	private Map<String,FacetSpec> _facetSpecMap;
	private Map<String, Integer> _origFacetSpecMaxCounts;
	private SenseiQuery _query;
	private int _offset;
	private int _count;
	private int _origOffset;
	private int _origCount;
	private boolean _fetchStoredFields;
	private Map<String,FacetHandlerInitializerParam> _facetInitParamMap;
	private Set<Integer> _partitions;
	private boolean _showExplanation;
	private static Random _rand = new Random(System.nanoTime());
	private String _routeParam;
	private String _groupBy;
	private int _maxPerGroup;
	private Set<String> _termVectorsToFetch;
	
	public SenseiRequest(){
		_facetInitParamMap = new HashMap<String,FacetHandlerInitializerParam>();
		_selections=new HashMap<String,BrowseSelection>();
		_sortSpecs=new ArrayList<SortField>();
		_facetSpecMap=new HashMap<String,FacetSpec>();
		_fetchStoredFields = false;
		_partitions = null;
		_showExplanation = false;
		_routeParam = null;
		_groupBy = null;
    _maxPerGroup = 0;
    _termVectorsToFetch = null;
	}

	public Set<String> getTermVectorsToFetch(){
	  return _termVectorsToFetch;
	}
	
	public void setTermVectorsToFetch(Set<String> termVectorsToFetch){
	  _termVectorsToFetch = termVectorsToFetch;
	}
/**
   * Get the transaction ID.
   * @return the transaction ID.
   */
  public final long getTid()
  {
    return tid;
  }

  /**
   * Set the transaction ID;
   * @param tid
   */
  public final void setTid(long tid)
  {
    this.tid = tid;
  }
  
  
	public boolean isShowExplanation() {
	  return _showExplanation;
    }

    public void setShowExplanation(boolean showExplanation) {
	  _showExplanation = showExplanation;
    }

	public void setPartitions(Set<Integer> partitions){
		_partitions = partitions;
	}
	
	public Set<Integer> getPartitions(){
		return _partitions;
	}

  public void setRouteParam(String routeParam)
  {
    _routeParam = routeParam;
  }

  public String getRouteParam()
  {
    if (_routeParam != null)
      return _routeParam;

    return String.valueOf(_rand.nextInt());
  }

  public void setGroupBy(String groupBy)
  {
    _groupBy = groupBy;
  }

  public String getGroupBy()
  {
    return _groupBy;
  }

  public void setMaxPerGroup(int maxPerGroup)
  {
    _maxPerGroup = maxPerGroup;
  }

  public int getMaxPerGroup()
  {
    return _maxPerGroup;
  }

	public Map<String,FacetHandlerInitializerParam> getFacetHandlerInitParamMap(){
		return _facetInitParamMap;
	}
	
	public void setFacetHandlerInitParamMap(Map<String,FacetHandlerInitializerParam> paramMap){
	  _facetInitParamMap = paramMap;
	}

	public void putAllFacetHandlerInitializerParams(Map<String,FacetHandlerInitializerParam> params){
		_facetInitParamMap.putAll(params);
	}
	
	public void setFacetHandlerInitializerParam(String name,FacetHandlerInitializerParam param){
		_facetInitParamMap.put(name, param);
	}
	
	public FacetHandlerInitializerParam getFacetHandlerInitializerParam(String name){
		return _facetInitParamMap.get(name);
	}

	public Set<String> getSelectionNames(){
		return _selections.keySet();
	}
	
	public void removeSelection(String name){
		_selections.remove(name);
	}
	
	public void setFacetSpecs(Map<String,FacetSpec> facetSpecMap)
	{
		_facetSpecMap = facetSpecMap;
	}
	
	public Map<String,FacetSpec> getFacetSpecs()
	{
		return _facetSpecMap;
	}
	
  public void saveState()
  {
    _origOffset = _offset;
    _origCount = _count;
    if (_origFacetSpecMaxCounts == null && _facetSpecMap != null)
    {
      _origFacetSpecMaxCounts= new HashMap<String, Integer>();
      for (Map.Entry<String, FacetSpec> entry : _facetSpecMap.entrySet())
      {
        FacetSpec spec = entry.getValue();
        if (spec != null)
        {
          _origFacetSpecMaxCounts.put(entry.getKey(), spec.getMaxCount());
        }
      }
    }
  }

	public void restoreState()
  {
    _offset = _origOffset;
    _count = _origCount;
    if (_facetSpecMap != null)
    {
      for (Map.Entry<String, FacetSpec> entry : _facetSpecMap.entrySet())
      {
        FacetSpec spec = entry.getValue();
        if (spec != null)
        {
          spec.setMaxCount(_origFacetSpecMaxCounts.get(entry.getKey()));
        }
      }
    }
  }

	public int getSelectionCount()
	{
		return _selections.size();
	}
	
	public void clearSelections(){
		_selections.clear();
	}
	
	/**
	 * Gets the number of facet specs
	 * @return number of facet pecs
	 * @see #setFacetSpec(String, FacetSpec)
	 * @see #getFacetSpec(String)
	 */
	public int getFacetSpecCount(){
		return _facetSpecMap.size();
	}
	
	public void clearSort(){
		_sortSpecs.clear();
	}
	
	public boolean isFetchStoredFields(){
		return _fetchStoredFields;
	}
	
	public void setFetchStoredFields(boolean fetchStoredFields){
		_fetchStoredFields = fetchStoredFields;
	}
	
	/**
	 * Sets a facet spec
	 * @param name field name
	 * @param facetSpec Facet spec
	 * @see #getFacetSpec(String)
	 */
	public void setFacetSpec(String name,FacetSpec facetSpec){
		_facetSpecMap.put(name,facetSpec);
	}
	
	/**
	 * Gets a facet spec
	 * @param name field name
	 * @return facet spec
	 * @see #setFacetSpec(String, FacetSpec)
	 */
	public FacetSpec getFacetSpec(String name){
		return _facetSpecMap.get(name);
	}
	
	/**
	 * Gets the number of hits to return. Part of the paging parameters.
	 * @return number of hits to return.
	 * @see #setCount(int)
	 */
	public int getCount() {
		return _count;
	}

	/**
	 * Sets the number of hits to return. Part of the paging parameters.
	 * @param count number of hits to return.
	 * @see #getCount()
	 */
	public void setCount(int count) {
		_count = count;
	}

	/**
	 * Gets the offset. Part of the paging parameters.
	 * @return offset
	 * @see #setOffset(int)
	 */
	public int getOffset() {
		return _offset;
	}

	/**
	 * Sets of the offset. Part of the paging parameters.
	 * @param offset offset
	 * @see #getOffset()
	 */
	public void setOffset(int offset) {
		_offset = offset;
	}

	/**
	 * Set the search query
	 * @param query query object
	 * @see #getQuery()
	 */
	public void setQuery(SenseiQuery query){
		_query=query;
	}
	
	/**
	 * Gets the search query
	 * @return query object
	 * @see #setQuery(SenseiQuery)
	 */
	public SenseiQuery getQuery(){
		return _query;
	}

  /**
   * Adds a browse selection array
   * @param selections selections to add
   * @see #addSelection(BrowseSelection)
   * @see #getSelections()
   */
  public void addSelections(BrowseSelection[] selections) {
    for (BrowseSelection selection : selections) {
      addSelection(selection);
    }
  }

	/**
	 * Adds a browse selection
	 * @param sel selection
	 * @see #getSelections()
	 */
	public void addSelection(BrowseSelection sel){
		_selections.put(sel.getFieldName(),sel);
	}
	
	/**
	 * Gets all added browse selections
	 * @return added selections
	 * @see #addSelection(BrowseSelection)
	 */
	public BrowseSelection[] getSelections(){
		return _selections.values().toArray(new BrowseSelection[_selections.size()]);
	}
	
	/**
	 * Gets selection by field name
	 * @param fieldname
	 * @return selection on the field
	 */
	public BrowseSelection getSelection(String fieldname){
	  return _selections.get(fieldname);
	}
	
	/**
	 * Add a sort spec
	 * @param sortSpec sort spec
	 * @see #getSort() 
	 * @see #setSort(SortField[])
	 */
	public void addSortField(SortField sortSpec){
		_sortSpecs.add(sortSpec);
	}

  /**
   * Add a sort spec
   * @param sortSpecs sort spec
   * @see #getSort()
   * @see #setSort(SortField[])
   */
  public void addSortFields(SortField[] sortSpecs){
    for (SortField field : sortSpecs) {
      addSortField(field);
    }
  }

	/**
	 * Gets the sort criteria
	 * @return sort criteria
	 * @see #setSort(SortField[])
	 * @see #addSortField(SortField)
	 */
	public SortField[] getSort(){
		return _sortSpecs.toArray(new SortField[_sortSpecs.size()]);
	}
	
	/**
	 * Sets the sort criteria
	 * @param sorts sort criteria
	 * @see #addSortField(SortField)
	 * @see #getSort()
	 */
	public void setSort(SortField[] sorts){
		_sortSpecs.clear();
		for (int i=0;i<sorts.length;++i){
			_sortSpecs.add(sorts[i]);
		}
	}
	
  /** Represents sorting by document score (relevancy). */
  public static final SortField FIELD_SCORE = new SortField (null, SortField.SCORE);
  public static final SortField FIELD_SCORE_REVERSE = new SortField (null, SortField.SCORE, true);

  /** Represents sorting by document number (index order). */
  public static final SortField FIELD_DOC = new SortField (null, SortField.DOC);
  public static final SortField FIELD_DOC_REVERSE = new SortField (null, SortField.DOC, true);

	@Override
	public String toString(){
	  StringBuilder buf=new StringBuilder();
	  if(_query != null)
	    buf.append("query: ").append(_query.toString()).append('\n');
    buf.append("page: [").append(_offset).append(',').append(_count).append("]\n");
    if(_sortSpecs != null)
      buf.append("sort spec: ").append(_sortSpecs).append('\n');
    if(_selections != null)
      buf.append("selections: ").append(_selections).append('\n');
    if(_facetSpecMap != null)
      buf.append("facet spec: ").append(_facetSpecMap).append('\n');
    if (_routeParam != null)
      buf.append("route param: ").append(_routeParam).append('\n');
    if (_groupBy != null)
      buf.append("group by: ").append(_groupBy).append('\n');
    buf.append("max per group: ").append(_maxPerGroup);
    buf.append("fetch stored fields: ").append(_fetchStoredFields);
    return buf.toString();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
	  return super.clone();
	}

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SenseiRequest)) return false;
    SenseiRequest b = (SenseiRequest)o;

    if (getCount() != b.getCount()) return false;
    if (getOffset() != b.getOffset()) return false;
    if (!facetSpecsAreEqual(getFacetSpecs(), b.getFacetSpecs())) return false;
    if (!selectionsAreEqual(getSelections(), b.getSelections())) return false;
    if (!initParamsAreEqual(getFacetHandlerInitParamMap(), b.getFacetHandlerInitParamMap())) return false;
    if (!Arrays.equals(getSort(), b.getSort())) return false;
    if (getQuery() == null) {
      if (b.getQuery() != null) return false;
    } else {
      if (!getQuery().toString().equals(b.getQuery().toString())) return false;
    }
    if (getGroupBy() == null) {
      if (b.getGroupBy() != null) return false;
    }
    else {
      if (!getGroupBy().equals(b.getGroupBy())) return false;
    }
    if (getMaxPerGroup() != b.getMaxPerGroup())
      return false;
    if (getPartitions() == null) {
      if (b.getPartitions() != null) return false;
    } else {
      if (!setsAreEqual(getPartitions(), b.getPartitions())) return false;
    }

    return true;
  }

  private boolean initParamsAreEqual(Map<String, FacetHandlerInitializerParam> a,
                                     Map<String, FacetHandlerInitializerParam> b) {
    if (a.size() != b.size()) return false;

    for (Entry<String,FacetHandlerInitializerParam> entry : a.entrySet()) {
      String key = entry.getKey();
      if (!b.containsKey(key)) return false;
      if (!areFacetHandlerInitializerParamsEqual(entry.getValue(), b.get(key))) return false;
    }

    return true;
  }

  private boolean areFacetHandlerInitializerParamsEqual(FacetHandlerInitializerParam a, FacetHandlerInitializerParam b) {
    if (!setsAreEqual(a.getBooleanParamNames(), b.getBooleanParamNames())) return false;
    if (!setsAreEqual(a.getIntParamNames(), b.getIntParamNames())) return false;
    if (!setsAreEqual(a.getDoubleParamNames(), b.getDoubleParamNames())) return false;
    if (!setsAreEqual(a.getLongParamNames(), b.getLongParamNames())) return false;
    if (!setsAreEqual(a.getStringParamNames(), b.getStringParamNames())) return false;
    if (!setsAreEqual(a.getByteArrayParamNames(), b.getByteArrayParamNames())) return false;

    for (String name : a.getBooleanParamNames()) {
      if (!Arrays.equals(a.getBooleanParam(name), b.getBooleanParam(name))) return false;
    }
    for (String name : a.getIntParamNames()) {
      if (!Arrays.equals(a.getIntParam(name), b.getIntParam(name))) return false;
    }
    for (String name : a.getDoubleParamNames()) {
      if (!Arrays.equals(a.getDoubleParam(name), b.getDoubleParam(name))) return false;
    }
    for (String name : a.getLongParamNames()) {
      if (!Arrays.equals(a.getLongParam(name), b.getLongParam(name))) return false;
    }
    for (String name : a.getStringParamNames()) {
      if (!Arrays.equals(a.getStringParam(name).toArray(new String[0]), b.getStringParam(name).toArray(new String[0]))) return false;
    }
/* NOT YET SUPPORTED
    for (String name : a.getByteArrayParamNames()) {
      assertTrue(Arrays.equals(a.getByteArrayParam(name), b.getByteArrayParam(name)));
    }
*/
    return true;
  }

  private boolean facetSpecsAreEqual(Map<String, FacetSpec> a, Map<String, FacetSpec> b) {
    if (a.size() != b.size()) return false;

    for (Entry<String,FacetSpec> entry : a.entrySet()) {
      String key = entry.getKey();
      if (!(b.containsKey(key))) return false;
      if (!facetSpecsAreEqual(entry.getValue(), b.get(key))) return false;
    }

    return true;
  }

  private boolean facetSpecsAreEqual(FacetSpec a, FacetSpec b) {
    return
        (a.getMaxCount() == b.getMaxCount())
        && (a.getMinHitCount() == b.getMinHitCount())
        && (a.getOrderBy() == b.getOrderBy())
        && (a.isExpandSelection() == b.isExpandSelection());
  }

  private boolean selectionsAreEqual(BrowseSelection[] a, BrowseSelection[] b) {
    if (a.length != b.length) return false;

    for (int i = 0; i < a.length; i++) {
      if (!selectionsAreEqual(a[i], b[i])) return false;
    }

    return true;
  }

  private boolean selectionsAreEqual(BrowseSelection a, BrowseSelection b) {
    return
        (a.getFieldName().equals(b.getFieldName()))
        && (Arrays.equals(a.getValues(), b.getValues()))
        && (Arrays.equals(a.getNotValues(), b.getNotValues()))
        && (a.getSelectionOperation().equals(b.getSelectionOperation()))
        && (a.getSelectionProperties().equals(b.getSelectionProperties()));
  }

  private <T> boolean setsAreEqual(Set<T> a, Set<T> b) {
    if (a.size() != b.size()) return false;

    Iterator<T> iter = a.iterator();
    while (iter.hasNext()) {
      T val = iter.next();
      if (!b.contains(val)) return false;
    }

    return true;
  }
  
  private static SenseiQuery buildSenseiQuery(String query,JSONObject params) throws Exception
  {
    SenseiQuery sq;

    JSONObject qjson = null;
    if (params==null){
    	qjson = new JSONObject();
    }
    else{
    	qjson = params;
    }
    
    if (query != null && query.length() > 0)
    {
      qjson.put("query", query);
    }
    
    sq = new SenseiJSONQuery(qjson);
    return sq;
  }
  
  private static String[] getStrings(JSONObject obj,String field){
	  String[] strArray = null;
	  JSONArray array = obj.optJSONArray(field);
	  if (array!=null){
		int count = array.length();
		strArray = new String[count];
		for (int i=0;i<count;++i){
			strArray[i] = array.optString(i);
		}
	  }
	  return strArray;
  }
  
  private static int[] getInts(JSONObject obj,String field,int defaultVal){
	  int[] intArray = null;
	  JSONArray array = obj.optJSONArray(field);
	  if (array!=null){
		int count = array.length();
		intArray = new int[count];
		for (int i=0;i<count;++i){
			intArray[i] = array.optInt(i,defaultVal);
		}
	  }
	  return intArray;
  }
  
  private static void applyFacetInitParams(DefaultFacetHandlerInitializerParam initParams,String type,JSONArray vals){
	  
  }
  
  public static SenseiRequest fromJSON(JSONObject json) throws Exception{
	  SenseiRequest req = new SenseiRequest();
	  
	  // query
	  String query = json.optString("query");
	  JSONObject qparams = json.optJSONObject("queryParams");
	  req.setQuery(buildSenseiQuery(query,qparams));
	  
	  // paging
	  JSONObject paging = json.optJSONObject("paging");
	  if (paging!=null){
		int count = paging.optInt("count", 10);
		int offset = paging.optInt("offset", 0);
		req.setCount(count);
		req.setOffset(offset);
	  }
	  
	  // group by
	  JSONObject groupBy = json.optJSONObject("groupBy");
	  if (groupBy!=null){
		  req.setGroupBy(groupBy.optString("field", null));
		  req.setMaxPerGroup(groupBy.optInt("count", 3));
	  }
	  
	  // selections
	  
	  JSONObject selections = json.optJSONObject("selections");
	  if (selections!=null){
		  Iterator<String> keyIter = selections.keys();
		  while (keyIter.hasNext()){
			  String field = keyIter.next();
			  BrowseSelection sel = new BrowseSelection(field);
			  JSONObject selObj = selections.getJSONObject(field);
			  if (selObj!=null){
				ValueOperation op = ValueOperation.ValueOperationOr;
				boolean isAnd = selObj.optBoolean("intersect",false);
				if (isAnd){
					op = ValueOperation.ValueOperationOr;
				}
				sel.setSelectionOperation(op);
				
				String[] vals = getStrings(selObj, "values");
				if (vals!=null && vals.length>0){
				   sel.setValues(vals);
				}
				
				String[] notVals = getStrings(selObj, "not");
				if (notVals!=null && notVals.length>0){
				   sel.setNotValues(notVals);
				}
				
				JSONObject propsObj = selObj.optJSONObject("props");
				if (propsObj!=null){
					Iterator<String> propsIter = propsObj.keys();
					while(propsIter.hasNext()){
					  String key = propsIter.next();
					  String val = propsObj.getString(key);
					  sel.setSelectionProperty(key, val);
					}
				}
			    req.addSelection(sel);
			  }
			  
		  }
	  }
	  
	  // facets
	  
	  JSONObject facets = json.optJSONObject("facets");
	  if (facets!=null){
		  Iterator<String> keyIter = facets.keys();
		  while (keyIter.hasNext()){
			  String field = keyIter.next();
			  JSONObject facetObj = facets.getJSONObject(field);
			  if (facetObj!=null){
				 FacetSpec facetSpec = new FacetSpec();
				 facetSpec.setMaxCount(facetObj.optInt("max", 10));
				 facetSpec.setMinHitCount(facetObj.optInt("minCount", 1));
				 facetSpec.setExpandSelection(facetObj.optBoolean("expand", false));
				 
				 String orderBy = facetObj.optString("orderBy", "hits");
				 FacetSpec.FacetSortSpec facetOrder = FacetSpec.FacetSortSpec.OrderHitsDesc;
				 if ("val".equals(orderBy)){
					 facetOrder = FacetSpec.FacetSortSpec.OrderValueAsc;
				 }
				 
				 facetSpec.setOrderBy(facetOrder);
				 req.setFacetSpec(field, facetSpec);
			  }
		  }
	  }
	  
	  // facet init params
	  JSONObject facetInit = json.optJSONObject("facetInit");
	  if (facetInit!=null){
		  Iterator<String> keyIter = facetInit.keys();
		  while (keyIter.hasNext()){
			  String field = keyIter.next();
			  JSONObject initParamObj = facetInit.getJSONObject(field);
			  if (initParamObj!=null){
				  
				  DefaultFacetHandlerInitializerParam initParam = new DefaultFacetHandlerInitializerParam();
				  Iterator<String> params = initParamObj.keys();
				  while(params.hasNext()){
					  String paramName = params.next();
					  String type = initParamObj.optString("type","string");
					  JSONArray vals = initParamObj.getJSONArray("values");
					  applyFacetInitParams(initParam,type,vals);
				  }
				  req.setFacetHandlerInitializerParam(field, initParam);  
			  }
		  }
	  }
	  // sorts
	  
	  JSONArray sortArray = json.optJSONArray("sorts");
	  if (sortArray!=null && sortArray.length()>0){
		  ArrayList<SortField> sortFieldList = new ArrayList<SortField>(sortArray.length());
		  for (int i=0;i<sortArray.length();++i){
		    JSONObject sortObj = sortArray.optJSONObject(i);
		    if (sortObj!=null){
		       String sortField = sortObj.getString("field");
		       if ("relevance".equals(sortField)){
		    	  sortFieldList.add(SortField.FIELD_SCORE);
		    	  continue;
		       }
		       else{
		    	  boolean rev = sortObj.optBoolean("reverse");
		    	  sortFieldList.add(new SortField(sortField,SortField.CUSTOM,rev));
		       }
		    }
		  }
		  if (sortFieldList.size()>0){
		    req.setSort(sortFieldList.toArray(new SortField[0]));
		  }
	  }
	  // other
	  
	  boolean fetchStored = json.optBoolean("fetchStored");
	  req.setFetchStoredFields(fetchStored);
	  
	  String[] termVectors = getStrings(json,"termVectors");
	  if (termVectors!=null && termVectors.length>0){
	    req.setTermVectorsToFetch(new HashSet<String>(Arrays.asList(termVectors)));
	  }
	  int[] partitions = getInts(json,"partitions",0);
	  if (partitions!=null && partitions.length>0){
		HashSet<Integer> partSet = new HashSet<Integer>(partitions.length);
		for (int part : partitions){
			partSet.add(part);
		}
	    req.setPartitions(partSet);
	  }
	  
	  boolean isExplain = json.optBoolean("explain",false);
	  req.setShowExplanation(isExplain);
	  
	  String routeParam = json.optString("routeParam",null);
	  req.setRouteParam(routeParam);
	  
	  return req;
  }

}
