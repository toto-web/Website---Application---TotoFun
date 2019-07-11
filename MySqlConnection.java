package com.bkav.aic.dbconnection;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bkav.aic.biobject.Regional;
import com.bkav.aic.control.Controller;
import com.bkav.aic.define.ErrorCode;
import com.bkav.aic.object.BIColumn;
import com.bkav.aic.object.BITable;
import com.bkav.aic.object.ChartObject;
import com.bkav.aic.object.DashboardConfig;
import com.bkav.aic.object.Field;
import com.bkav.aic.object.Formula;
import com.bkav.aic.object.FormulaChart;
import com.bkav.aic.object.FormulaChartDetail;
import com.bkav.aic.object.Group;
import com.bkav.aic.object.Statistical;
import com.bkav.aic.object.Statistical_Dashboard;
import com.bkav.aic.object.Statistical_Widget;
import com.bkav.aic.object.User;

public class MySqlConnection {

	public MySqlConnection() {
	}
	public Formula getFormulaById(int sttdId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Formula.class);
		crit.add(Restrictions.eq("sttcId", sttdId));
		List<Formula> list = crit.list();
		if(list.size() != 1){
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return null;
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list.get(0);
	}
	public int insert(Object obj) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();

		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return ErrorCode.OK.code;
		} catch (Exception e) {
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			e.printStackTrace();
			return ErrorCode.Not_OK.code;
		}
	}
	
	public int insertInWso2(Object obj) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();

		SessionFactory factory = HibernateUtil.popWso2ResourceConfigSessionFactory();
		Session session = factory.openSession();
		try {
			session.beginTransaction();
			session.save(obj);
			session.getTransaction().commit();
			session.close();
			HibernateUtil.putWso2ConfigSessionFactory(factory);
			return ErrorCode.OK.code;
		} catch (Exception e) {
			session.close();
			HibernateUtil.putWso2ConfigSessionFactory(factory);
			e.printStackTrace();
			return ErrorCode.Not_OK.code;
		}
	}
	
	public <T> int update(Class<T> clazz, T obj) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		session.update(obj);
		session.getTransaction().commit();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return ErrorCode.OK.code;
	}
	
	public <T> int updateInWso2(Class<T> clazz, T obj) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popWso2ResourceConfigSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		session.update(obj);
		session.getTransaction().commit();
		session.close();
		HibernateUtil.putWso2ConfigSessionFactory(factory);
		return ErrorCode.OK.code;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getListAll(Class<T> clazz) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(clazz);
		List<T> result = new ArrayList<T>();
		try {
			result = (List<T>) ctr.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return result;
	}
//
//	@SuppressWarnings("unchecked")
//	public <T> List<T> getListPaging(Class<T> clazz, int start, int max) {
//		// Session session =
//		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
//		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
//		Session session = factory.openSession();
//		Criteria ctr = session.createCriteria(clazz);
//		ctr.setFirstResult(start);
//		ctr.setMaxResults(max);
//		List<T> result = new ArrayList<T>();
//		try {
//			result = (List<T>) ctr.list();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		session.close();
//		HibernateUtil.putConfigSessionFactory(factory);
//		return result;
//	}

	@SuppressWarnings("unchecked")
	public <T> int delete(Class<T> clazz, Serializable id) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		T t = (T) session.get(clazz, id);
		int rs;
		if (t != null) {
			session.delete(t);
			rs = ErrorCode.OK.code;
		} else
			rs = ErrorCode.Not_OK.code;
		session.getTransaction().commit();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public <T> int deleteInWso2(Class<T> clazz, Serializable id) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popWso2ResourceConfigSessionFactory();
		Session session = factory.openSession();
		session.beginTransaction();
		T t = (T) session.get(clazz, id);
		int rs;
		if (t != null) {
			session.delete(t);
			rs = ErrorCode.OK.code;
		} else
			rs = ErrorCode.Not_OK.code;
		session.getTransaction().commit();
		session.close();
		HibernateUtil.putWso2ConfigSessionFactory(factory);
		return rs;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, Serializable id) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;
		T t = null;
		try {
			tx = session.beginTransaction();
			t = (T) session.get(clazz, id);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInWso2(Class<T> clazz, Serializable id) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popWso2ResourceConfigSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;
		T t = null;
		try {
			tx = session.beginTransaction();
			t = (T) session.get(clazz, id);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		}
		session.close();
		HibernateUtil.putWso2ConfigSessionFactory(factory);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getFromBI(Class<T> clazz, Serializable id) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popBIFormSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;
		T t = null;
		try {
			tx = session.beginTransaction();
			t = (T) session.get(clazz, id);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Set<T> getAllByIDList(Class<T> clazz, Set<Serializable> idList) {
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Transaction tx = null;
		
		Set<T> tList = new HashSet<T>();
		try {
			tx = session.beginTransaction();
			for (Serializable id:idList){
				tList.add((T) session.get(clazz, id));
			}
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return tList;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getListFK_DataOfUser(Class<T> clazz, String userId) {
		List<T> listCT = new ArrayList<>();
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Formula.class);
		ctr.add(Restrictions.eq("userId", userId));
		listCT = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return listCT;
	}

	

	
	@SuppressWarnings("unused")
	private final int _____Chart_____ = 0;
	
	@SuppressWarnings("unchecked")
	public List<FormulaChart> getChartBySttc(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(FormulaChart.class);
		crit.add(Restrictions.eq("sttcId", sttcId));
		List<FormulaChart> list = crit.list();
		List<String> sql = new ArrayList<String>();
			
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public ChartObject getChartObjectBySttcId(int sttcId) {
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(ChartObject.class);

		ctr.add(Restrictions.eq("sttcId", sttcId));
		List<ChartObject> list = ctr.list();
		if (list.size() != 1){
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return null;
		}
		ChartObject chart = list.get(0);
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return chart;
	}
	
	@SuppressWarnings("unchecked")
	public List<FormulaChartDetail> getChartDetailById(int chartId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(FormulaChartDetail.class);
		crit.add(Restrictions.eq("chartid", chartId));
		List<FormulaChartDetail> list = crit.list();
		
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list;
	}

	@SuppressWarnings("unused")
	private final int _____BIData_____ = 0;
	
	@SuppressWarnings("unchecked")
	public List<BIColumn> getListBIColumnByTableID(int tableID) {
		List<BIColumn> list = new ArrayList<>();
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(BIColumn.class);
		ctr.add(Restrictions.eq("TableID", tableID));
		list = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list;
	}	
	
	public BITable getTableByTableName(String tableName)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(BITable.class);
		ctr.add(Restrictions.eq("tableName", tableName));
		List<BITable> list = ctr.list();
		if (list.size() != 1){
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return null;
		}
			
		BITable table = list.get(0);
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return table;
	}

	@SuppressWarnings("unused")
	private final int _____regional_____ = 0;

	@SuppressWarnings("unchecked")
	public List<String> getListOfCity() {
		SessionFactory factory = HibernateUtil.popBIFormSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Regional.class);
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.eq("level", 2)); // 2 -> tinh
		or.add(Restrictions.eq("level", 1));
		ctr.add(or);
		List<Regional> list = ctr.list();

		List<String> rs = new ArrayList<String>();

		for (Regional region : list) {
			JSONObject json = new JSONObject();
			try {
				json.put("regional_id", region.getRegionalID());
				json.put("name", region.getRegionalName());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rs.add(json.toString());
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return rs;
	}

	@SuppressWarnings("unchecked")
	public List<String> getListDistrictOfCity(int cityId) {
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Regional.class);

		ctr.add(Restrictions.eq("level", 3)); // 3 -> quan huyen
		ctr.add(Restrictions.eq("parent_id", cityId));

		List<String> rs = new ArrayList<String>();
		try {
			List<Regional> list = ctr.list();
			JSONObject jsonall = new JSONObject();
			jsonall.put("regional_id", cityId);
			jsonall.put("name", "Tất cả");
			rs.add(jsonall.toString());
			for (Regional region : list) {
				JSONObject json = new JSONObject();
				json.put("regional_id", region.getRegionalID());
				json.put("name", region.getRegionalName());
				rs.add(json.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return rs;
	}
	
	public String getJSONFieldsLevel()
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Field.class);
		crit.add(Restrictions.eq("level", 1));
		List<Field> listFieldLV1 = crit.list();
		//System.out.println("parent size: "+ listFieldLV1.size());
		JSONArray jsonarr = new JSONArray();
		for(Field field : listFieldLV1)
		{
			try {
				JSONObject json = new JSONObject();
				json.put("id", field.getId());
				json.put("name", field.getName());
				Criteria crit2 = session.createCriteria(Field.class);
				crit2.add(Restrictions.eq("level", 2));
				crit2.add(Restrictions.eq("parentsId", field.getId()));
				List<Field> listChild = crit2.list();
			//	System.out.println("child size: "+ listChild.size());
				JSONArray jsonArrChild = new JSONArray();
				for(Field child : listChild)
				{
					
					JSONObject json2 = new JSONObject();
					json2.put("id", child.getId());
					json2.put("name", child.getName());
					jsonArrChild.put(json2);
				}
				json.put("child", jsonArrChild);
				jsonarr.put(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return jsonarr.toString();
	}
	
	@SuppressWarnings("unused")
	private final int _____Group_____ = 0;
	
	public List<Group> getListGroupByUserId(String userId, int fieldId) {
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Group.class);
		User user = get(User.class, userId);
		if(user.getPermission() == 1){
			ctr.add(Restrictions.eq("userId", "admin"));
			ctr.add( Restrictions.eq("fieldId", fieldId));
		}
			
		else{
			Criterion crit1 =  Restrictions.eq("userId", "admin");
			Criterion crit2 =  Restrictions.eq("userId", userId);
			LogicalExpression orExp = Restrictions.or(crit1, crit2);
			ctr.add(orExp);
			ctr.add( Restrictions.eq("fieldId", fieldId));
		}
		List<Group> grp = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return grp;
	}
	@SuppressWarnings("unchecked")
	public <T> List<T> getListPagingForUser(Class<T> clazz, int start, int max, String userId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(clazz);
		ctr.add(Restrictions.eq("userId", userId));
		ctr.setFirstResult(start);
		ctr.setMaxResults(max);
		List<T> result = new ArrayList<T>();
		try{
			result = (List<T>) ctr.list();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return result;
	}
	@SuppressWarnings("unchecked")
	public List<Statistical> getListFK_StatisticalOfGroup(int groupID) {
		List<Statistical> listTK = new ArrayList<>();
		// Session session =
		// HibernateUtil.getSessionFactory("hibernate.cfg.xml").openSession();
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Statistical.class);
		ctr.add(Restrictions.eq("groupId", groupID));
		listTK = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return listTK;
	}
	@SuppressWarnings("unused")
	private final int _____Statistical_____ = 0;
	
	@SuppressWarnings("unchecked")
	public List<Statistical> getAllStatisticalOfGroup(int grpId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Statistical.class);
		crit.add(Restrictions.eq("groupId", grpId));
		List<Statistical> list = crit.list();
		List<String> sql = new ArrayList<String>();
			
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list;
	}
	@SuppressWarnings("unused")
	private final int _____Formula_____ = 0;
	public Formula getFormulaBySttc(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Formula.class);
		crit.add(Restrictions.eq("sttcId", sttcId));
		List<Formula> list = crit.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list.get(0);
	}
	
	@SuppressWarnings("unused")
	private final int _____department_____ = 0;
	
	public List<Field> getListFieldByUserId(String userId) {
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Field.class);
		User user = get(User.class, userId);
		if(user.getPermission() == 1) ctr.add(Restrictions.eq("userId", "admin"));
		else{
			Criterion crit1 =  Restrictions.eq("userId", "admin");
			Criterion crit2 =  Restrictions.eq("userId", userId);
			LogicalExpression orExp = Restrictions.or(crit1, crit2);
			ctr.add(orExp);
		}
		List<Field> grp = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return grp;
	}
	public List<Field> getListFieldByGroupId(String fieldId) {
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Field.class);
		ctr.add(Restrictions.eq("id", fieldId));
		List<Field> grp = ctr.list();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return grp;
	}
	
	@SuppressWarnings("unused")
	private final int _____WSO2_____ = 0;
	public String getURLDashboardBySttcId(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria ctr = session.createCriteria(Statistical_Dashboard.class);
		ctr.add(Restrictions.eq("statisticalId", sttcId));
		List<Statistical_Dashboard> list = ctr.list();
		if(list.size() == 0 || list.size() > 1) {
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return "";
		}
		
		Statistical_Dashboard sttcdb = list.get(0);
		String url = sttcdb.getURL();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return url;
	}
	@SuppressWarnings("unchecked")
	public List<String> getWidgetIdBySttc(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Statistical_Widget.class);
		crit.add(Restrictions.eq("statisticalId", sttcId));
		List<Statistical_Widget> list = crit.list();
		List<String> rs = new ArrayList<String>();
		for(Statistical_Widget sta_wg :  list)
		{
			rs.add(sta_wg.getWIDGET_ID());
		}
		
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return rs;
	}
	@SuppressWarnings("unchecked")
	public String getDashboardIdBySttc(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(Statistical_Dashboard.class);
		crit.add(Restrictions.eq("statisticalId", sttcId));
		List<Statistical_Dashboard> list = crit.list();
		
		if(list.size() != 1){
			session.close();
			HibernateUtil.putConfigSessionFactory(factory);
			return "";
		}
		
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return list.get(0).getURL();
	}
	
	@SuppressWarnings("unused")
	private final int _____SQL_____ = 0;
	
	
	
	@SuppressWarnings("unchecked")
	public List<String> getQueryBySttc(int sttcId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(FormulaChart.class);
		crit.add(Restrictions.eq("sttcId", sttcId));
		List<FormulaChart> list = crit.list();
		List<String> sql = new ArrayList<String>();
		for(FormulaChart chart : list){
			sql.add(chart.getSql());
		}
			
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		return sql;
	}
	//return first row of sql result
	@SuppressWarnings("rawtypes")
	public String executeNativeSQL(String sql) {
		SessionFactory factory = HibernateUtil.popBIFormSessionFactory();
		Session session = factory.openSession();
//		List<String> rs = new ArrayList<>();
		String rs = "";
		try {
			Query query = session.createSQLQuery(sql);
			List list = query.list();
//			rs = new ArrayList<>();
//			for (int i = 0; i < list.size(); i++) {
//				rs.add(list.get(i).toString());
//			}
			if(list.size() >= 1) rs = list.get(0).toString(); 
			//rs = list.toString();
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);
			return "";
		}
	}
	
	public int createViewsTable(String viewName, String sql) {
		SessionFactory factory = HibernateUtil.popBIFormSessionFactory();
		Session session = factory.openSession();
		try {
			
			
			System.out.println("create view: CREATE VIEW "+viewName+" AS "+ sql);
			Query query = session.createSQLQuery("CREATE OR REPLACE VIEW "+viewName+" AS "+ sql);
			
			int rs =  query.executeUpdate();
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);

			return -1;
		}
	}
	
	public <T> long countRecordOfTable(Class<T> clazz)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		
		long count = (long) session.createCriteria(clazz)
        .setProjection(Projections.rowCount())
        .uniqueResult();
		session.close();
		HibernateUtil.putConfigSessionFactory(factory);
		
		return count;
	}
	
	public <T> long countRecordOfTableInWSO2(Class<T> clazz)
	{
		SessionFactory factory = HibernateUtil.popWso2ResourceConfigSessionFactory();
		Session session = factory.openSession();
		
		long count = (long) session.createCriteria(clazz)
        .setProjection(Projections.rowCount())
        .uniqueResult();
		session.close();
		HibernateUtil.putWso2ConfigSessionFactory(factory);
		
		return count;
	}
	
	public String getColumnResult(String sql)
	{
		SessionFactory factory = HibernateUtil.popBIFormSessionFactory();
		Session session = factory.openSession();
		JSONArray arr = new JSONArray();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			
			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<String> rs = new ArrayList<>();
			List<HashMap<String,String>> maplist = new ArrayList<HashMap<String, String>>();
			maplist=query.list();
			for(HashMap<String,String> map : maplist)
			{
				
				JSONObject jsonData = new JSONObject();
				Set<String> keys = map.keySet();
				List<String> listKeys = new ArrayList<String>(keys);

				for(int i = 0 ; i < listKeys.size() ; i++)
				{
					jsonData.put(listKeys.get(i), map.get(listKeys.get(i)));
				}
				
				
				arr.put(jsonData);
			}
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);
			return arr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			session.close();
			HibernateUtil.putBIFormSessionFactory(factory);

			return "";
		}
	}

	public DashboardConfig getDashboardConfig(String userId, int fieldId)
	{
		SessionFactory factory = HibernateUtil.popConfigSessionFactory();
		Session session = factory.openSession();
		Criteria crit = session.createCriteria(DashboardConfig.class);
		crit.add(Restrictions.eq("userId", userId));
		crit.add(Restrictions.eq("fieldId", fieldId));
		List<DashboardConfig> list = crit.list();
		session.close();//sttc.getUserId()
		HibernateUtil.putConfigSessionFactory(factory);
		if(list.size() != 1) return null;
		return list.get(0);
	}

	public static void main(String[] args) {
		MySqlConnection con = new MySqlConnection();
		Controller ctrl = new Controller();
		//List<Formula> list = con.getListAll(Formula.class);
		//System.out.println(con.getJSONFieldsLevel());
		FormulaChart  chart = con.get(FormulaChart.class, 1);
		 
		try {
			JSONArray	array = new JSONArray(con.getColumnResult(chart.getSql()));
			System.out.println(array.getJSONObject(0));
			List<String> cloumn = ctrl.keyJSONOBJ(array.getJSONObject(0));
			System.out.println("size: " + cloumn.size());
			for(String name : cloumn){
				System.out.println("column: " + name);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("printStackTrace: " );
		}
		
		
		//System.out.println(con.getColumnResult(chart.getSql()));
		
		//System.out.println(getListDepartmentByUserId("dungdt").toString());
	}
}
