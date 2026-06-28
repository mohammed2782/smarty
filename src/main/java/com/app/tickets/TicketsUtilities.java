package com.app.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.app.util.Notifications;

import com.app.util.Utilities;

import smarty.core.CoreUtilities;

public class TicketsUtilities {
	List<String> ranksThatAreChatControllers = Arrays.asList("ITBOSS", "MANAGER", "CALL_CENTER");
	private LinkedList<TicketBean> newTickets;
	private LinkedList<TicketBean> inProcessTickets;
	private LinkedList<TicketBean> closedTickets;
	public  final String sqlGetChatInfo = "select chat_ticketid, chat_otherpartyid, chat_otherpartyrank from p_tickets_chat where chat_id=? ";

	private  final String sqlTickets = " select tkt_id, subjects.kbdesc as ticketsubject, tkt_subject, tkt_description, tkt_createdby, tkt_createddt, "
			+ " tkt_status, tkt_relatedcustomer,tkt_relatedshop , concat(mcust_name, ' - ', cust_name) as customer, closers.us_name as closedby, "
			+ " tkt_createfromsys, tkt_closedby , tkt_closeddt, tkt_priority, tkt_ownerbranch, tkt_forcase, tkt_relatedagent, tkt_relatedbranch, "
			+ " tkt_assignedemp, tkt_closeremarks, "
			+ " (case when tkt_creatortype  in ('STAFF', 'AGENT') then ( select us_name from kbusers where us_id =  tkt_createdby) "
			+ "     when  tkt_creatortype in ('MASTERCUST' , 'MASTERCUSTOMER') then ( select mcust_name from kb_mastercustomer where mcust_id =  tkt_createdby) "
			+ "     when  tkt_creatortype = 'CUSTOMER' then ( select cust_name from kbcustomers where cust_id =  tkt_createdby) "
			+ " else 'ERROR' end) as createdby, tkt_creatortype, branch_name, "
			+ " (select count(*) from p_tickets_chat where chat_ticketid= tkt_id and chat_seen_bymain_system='N') as chat_not_seen_bymain  ,"
			+ " ifnull(chat_seen_by_otherparty,'N') as chat_seen_by_otherparty, tkt_reopened "
			+ " from  p_tickets "
			+ " join kbbranches on branch_id =  tkt_ownerbranch "
			+ " left join p_tickets_chat "
			+ "		on (chat_ticketid = tkt_id and chat_otherpartyid=? and chat_otherpartyrank='BRANCH' "
			+ "      and chat_otherparty_chat_status=? and chat_otherpartyassigned_emp>0) "
			+ " left join kbusers closers on closers.us_id = tkt_closedby and closers.us_rank not in ('DLVAGENT', 'MASTERCUSTOMER') "
			+ " left join kbgeneral subjects on subjects.kbcode = tkt_subject and subjects.kbcat1='TICKET' and subjects.kbcat2='SUBJECTS' "
			+ " left join kb_mastercustomer  on mcust_id = tkt_relatedcustomer  "
			+ " left join kbcustomers  on cust_id = tkt_relatedshop  "
			+ " where tkt_status !='CLS' and "
			+ "  ( tkt_status = ? and (tkt_assignedemp =? and 1=?)  "
			+ "		  or ( chat_otherpartyid =? )"
			+ "   ) "
			+ " order by  chat_not_seen_bymain desc ";

	private  final String sqlClosedXDaysTickets = " select tkt_id, '' as ticketdesc, subjects.kbdesc as ticketsubject, tkt_subject, tkt_description, tkt_createdby, tkt_createddt, "
			+ " tkt_status, tkt_relatedcustomer,tkt_relatedshop , concat(mcust_name, ' - ', cust_name) as customer, closers.us_name as closedby, "
			+ " tkt_createfromsys, tkt_closedby , tkt_closeddt, tkt_priority, tkt_ownerbranch, tkt_forcase, tkt_relatedagent, tkt_relatedbranch, "
			+ " tkt_assignedemp, tkt_closeremarks, "
			+ " (case when tkt_creatortype  in ('STAFF', 'AGENT') then ( select us_name from kbusers where us_id =  tkt_createdby) "
			+ "     when  tkt_creatortype in ('MASTERCUST' , 'MASTERCUSTOMER') then ( select mcust_name from kb_mastercustomer where mcust_id =  tkt_createdby) "
			+ "     when  tkt_creatortype = 'CUSTOMER' then ( select cust_name from kbcustomers where cust_id =  tkt_createdby) "
			+ " else 'ERROR' end) as createdby, tkt_creatortype, branch_name  " + " from " + " p_tickets "
			+ " join kbbranches on branch_id =  tkt_ownerbranch "
			+ " left join kbusers closers on closers.us_id = tkt_closedby and closers.us_rank not in ('DLVAGENT', 'MASTERCUSTOMER') "
			+ " left join kbgeneral subjects on subjects.kbcode = tkt_subject and subjects.kbcat1='TICKET' and subjects.kbcat2='SUBJECTS' "
			+ " left join kb_mastercustomer  on mcust_id = tkt_relatedcustomer  "
			+ " left join kbcustomers  on cust_id = tkt_relatedshop  "
			+ " where  tkt_assignedemp = ? and tkt_status = ? and tkt_closeddt >= date_add(now(), interval -? day) ";
	
	private static final String saveTicketHistory ="insert into p_tickets_hist ("
			+ "tkt_id, tkt_subject, tkt_othersubject, tkt_status, tkt_closedby, tkt_closeddt, tkt_createdby, tkt_createddt, tkt_parentticket,"
			+ " tkt_priority, tkt_ownerbranch, tkt_forcase, tkt_relatedcustomer, tkt_relatedshop, tkt_relatedagent, tkt_relatedbranch, tkt_assignedemp,"
			+ " tkt_description, tkt_closeremarks, tkt_creatortype, tkt_createfromsys, tkt_code, tkt_stage, tkt_step, tkt_rank, tkt_action, tkt_previous_tktid,"
			+ " tkt_previous_tktcode, tkt_enterdate, tkt_action_takenby,"
			+ " tkt_previous_action_taken_by, tkt_customerlastestrate, tkt_reopened,"
			+ "  tkt_hist_latest_action, tkt_hist_createddt, tkt_hist_createdby "
			+ ")"
			+ "select tkt_id, tkt_subject, tkt_othersubject, tkt_status, tkt_closedby, tkt_closeddt, tkt_createdby, tkt_createddt, tkt_parentticket, "
			+ "tkt_priority, tkt_ownerbranch, tkt_forcase, tkt_relatedcustomer, tkt_relatedshop, tkt_relatedagent, tkt_relatedbranch, tkt_assignedemp, "
			+ " tkt_description, tkt_closeremarks, tkt_creatortype, tkt_createfromsys, tkt_code, tkt_stage, tkt_step, tkt_rank, tkt_action, tkt_previous_tktid, "
			+ " tkt_previous_tktcode, tkt_enterdate, tkt_action_takenby, tkt_reopened, "
			+ "tkt_previous_action_taken_by, tkt_customerlastestrate,"
			+ "  ? 					, now()					, ?  from p_tickets where tkt_id = ?";

	public static String getLastPersonWhoChangedTicketOwner(Connection a_conn, int a_ticketId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String name = null;
		try {
			pst = a_conn.prepareStatement(
					"select us_name From p_tickets_hist join kbusers on us_id = tkt_hist_createdby  "
					+ " where tkt_hist_latest_action = 'CHANGE_OWNER' and tkt_id = ? order by tkt_hist_id desc limit 1  ");
			pst.setInt(1, a_ticketId);
			rs = pst.executeQuery();
			if (rs.next()) {
				name = rs.getString("us_name");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return name;
	}
	
	public static void saveTicketInHistory(Connection a_conn, int a_ticketId, String a_action, int a_actionTakenBy) throws Exception {
		PreparedStatement pst = null;
		try {
			//System.out.println(saveTicketHistory);
			pst = a_conn.prepareStatement(saveTicketHistory);
			pst.setString(1, a_action);
			pst.setInt(2, a_actionTakenBy);
			pst.setInt(3, a_ticketId);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {pst.close();} catch (Exception e) {}
		}
	}
	
	public static void changeTicketOwenr(Connection a_conn, int a_ticketId, int a_toUser, int a_actionBy) throws Exception {
		PreparedStatement pst = null;
		try {
			saveTicketInHistory(a_conn, a_ticketId, "CHANGE_OWNER", a_actionBy);
			pst = a_conn.prepareStatement(
					"update p_tickets left join p_tickets_chat on tkt_id= chat_ticketid"
					+ " set tkt_assignedemp=? , chat_seen_bymain_system='N' where tkt_id=? ");
			pst.setInt(1, a_toUser);
			pst.setInt(2, a_ticketId);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void closeTicket(Connection a_conn, int a_ticketId, int a_actionBy) throws Exception {
		PreparedStatement pst = null;
		try {
			saveTicketInHistory(a_conn, a_ticketId, "CLOSE_TICKET", a_actionBy);
			pst = a_conn.prepareStatement(
					"update p_tickets set tkt_status='CLS', tkt_closedby=? , tkt_closeddt=now() where tkt_id=? ");
			pst.setInt(1, a_actionBy);
			pst.setInt(2, a_ticketId);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	public TicketBean getSingleTicketInfo(Connection conn, int ticketId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		TicketBean ticketBean = new TicketBean();
		try {
			pst = conn.prepareStatement("select tkt_createfromsys, tkt_reopened, tkt_status, tkt_id, tkt_createddt, tkt_priority,"
					+ " subjects.kbdesc as ticketsubject, tkt_forcase, c_assignedagent, c_custreceiptnoori "
					+ " from p_tickets " + " left join p_cases on c_id = tkt_forcase "
					+ " left join kbgeneral subjects on subjects.kbcode = tkt_subject and subjects.kbcat1='TICKET' and subjects.kbcat2='SUBJECTS' "
					+ " where tkt_id=?");
			pst.setInt(1, ticketId);
			rs = pst.executeQuery();
			if (rs.next()) {
				// ticketBean.setTktOwnerBranchName(rs.getString("branch_name")); //الفرع
				// ticketBean.setTktCreatedByName(rs.getString("createdby")); //منشأ التذكرة
				// ticketBean.setTktCaseId(rs.getInt("tkt_forcase")); // الى شحنه
				ticketBean.setTktId(rs.getInt("tkt_id")); // رقم التذكرة
				ticketBean.setTktDate(rs.getString("tkt_createddt")); // تاريخ ووقت الأنشاء
				ticketBean.setTktTitle(rs.getString("ticketsubject"));
				ticketBean.setTktPriorityCode(rs.getString("tkt_priority"));
				ticketBean.setTktCaseId(rs.getInt("tkt_forcase"));
				ticketBean.setAssignedAgent(rs.getInt("c_assignedagent"));
				ticketBean.setOriReceiptNo(rs.getString("c_custreceiptnoori"));
				ticketBean.setTktStatusCode(rs.getString("tkt_status"));
				ticketBean.setReOpened(rs.getString("tkt_reopened"));
				ticketBean.setCreatedFromSystem(rs.getString("tkt_createfromsys"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return ticketBean;
	}

	public ArrayList<Integer> getNewTicketsNotAssigned(Connection aConn, String a_statesCommaSeperated, String a_searchMode, int aBranchId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<Integer> newTicketsList = new ArrayList<Integer>();
		try {
			String sql = "";
			if (aBranchId == 1) {
				sql = "select  tkt_id from  p_tickets where tkt_assignedemp =0 and tkt_status = 'NEW' ";
				if(a_statesCommaSeperated !=null && a_statesCommaSeperated.length()>0) {
					sql = "select  tkt_id from p_tickets ";
					if (a_searchMode == null || a_searchMode.equalsIgnoreCase("in_states")) {
						sql +="  join p_cases on (tkt_forcase = c_id and c_rcv_state  in ("+a_statesCommaSeperated+") )";
					}else if (a_searchMode.equalsIgnoreCase("not_in_states")) {
						sql +="  join p_cases on (tkt_forcase = c_id and c_rcv_state not in ("+a_statesCommaSeperated+") )";
					}else {
						;
					}
					sql += " where tkt_assignedemp =0 and tkt_status = 'NEW'";
				}
				if (a_searchMode == null || a_searchMode.equalsIgnoreCase("no_cases_attached")) {
					sql += " and tkt_forcase = 0  ";
				}
				//System.out.println("sql--->"+sql);
			} else {
				sql = "select  tkt_id from  p_tickets join p_tickets_chat on tkt_id = chat_ticketid  "
						+ " where chat_otherpartyassigned_emp =0 "
						+ " and chat_otherparty_chat_status = 'NEW' "
						+ " and tkt_status != 'CLS' "
						+ " and chat_otherpartyid=? "
						+ " and chat_otherpartyrank='BRANCH'";
			}
			pst = aConn.prepareStatement(sql);
			if (aBranchId != 1) {
				pst.setInt(1, aBranchId);
			}
			rs = pst.executeQuery();
			while (rs.next())
				newTicketsList.add(rs.getInt("tkt_id"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return newTicketsList;
	}

	public ArrayList<Integer> checkTicketsUnseenChatsByController(Connection conn, int userId, int aBranchId)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<Integer> ticketsList = new ArrayList<Integer>();
		try {
			String sql = "";
			if (aBranchId == 1) {
				sql = "select distinct(tkt_id) as tktid "
						+ " from p_tickets_chat join p_tickets on tkt_id = chat_ticketid "
						+ "where chat_seen_bymain_system = 'N' and tkt_assignedemp =? and tkt_status = 'OPEN' ";
			} else {
				sql = "select distinct(tkt_id) as tktid " + " from p_tickets_chat "
						+ " join p_tickets on tkt_id = chat_ticketid "
						+ " where chat_seen_by_otherparty = 'N' "
						+ " and chat_otherpartyassigned_emp =? "
						+ " and chat_otherparty_chat_status = 'OPEN' "
						+ " and tkt_status != 'CLS' "
						+ " and chat_otherpartyid=? "
						+ " and chat_otherpartyrank='BRANCH' ";
			}
			pst = conn.prepareStatement(sql);
			pst.setInt(1, userId);
			if (aBranchId != 1) {
				pst.setInt(2, aBranchId);
			}
			rs = pst.executeQuery();
			while (rs.next())
				ticketsList.add(rs.getInt("tktid"));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return ticketsList;
	}

	public boolean assignAndOpenTicket(Connection conn, int userId, int ticketId, int aBranchCode) throws Exception {
		PreparedStatement pst = null;
		boolean anyUpdated = false;
		try {
			String sql="update p_tickets set tkt_status = 'OPEN', tkt_assignedemp=? where tkt_status = 'NEW' and tkt_assignedemp=0 and tkt_id=? ";
			if (aBranchCode != 1) {
				sql = "update p_tickets_chat set chat_otherparty_chat_status='OPEN', chat_otherpartyassigned_emp=?, chat_seen_by_otherparty='Y' "
						+ " where chat_otherparty_chat_status = 'NEW' and chat_ticketid =? and chat_otherpartyid=?"
						+ " and chat_otherpartyrank ='BRANCH'";
			}
			pst = conn.prepareStatement(sql);
			pst.setInt(1, userId);
			pst.setInt(2, ticketId);
			if (aBranchCode != 1) {
				pst.setInt(3, aBranchCode);
			}
			int rowsUpd = pst.executeUpdate();
			if (rowsUpd > 0)
				anyUpdated = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return anyUpdated;
	}

	public String getSingleTicketHtml(TicketBean ticketBean, boolean showActionButtons, boolean dragable)
			throws Exception {
		StringBuilder sb = new StringBuilder("");
		String clickAbleTicket = "";
		String priority = "<span class='badge rounded-pill bg-danger' style='margin-right: 0px;font-size: 9px;margin-top: 5px;'>عاجل</span>";
		if (ticketBean.getTktPriorityCode().equalsIgnoreCase("MED"))
			priority = "<span class='badge rounded-pill bg-warning text-dark' style='margin-right: 0px;font-size: 9px;margin-top: 5px;'>متوسطة</span>";
		else if (ticketBean.getTktPriorityCode().equalsIgnoreCase("LOW"))
			priority = "<span class='badge rounded-pill bg-primary' style='margin-right: 0px;font-size: 9px;margin-top: 5px;'>عادية</span>";
		StringBuilder actionButtons = new StringBuilder("");
		String dragAttributes = "";
		
		String reOpenedStatus = "";
		String showDragButton = "";
		if (dragable) {
			dragAttributes = "draggable='true'  ondragstart='drag(event)'";
			showDragButton = "<button type=\"button\" id='open-ticket-btn-" + ticketBean.getTktId()
			+ "' onclick='doThisLikeDrag(" + ticketBean.getTktId()+ ");'"
			+ " style=\"font-size:11px; background-color:#007381 !important\" class=\"btn btn-icon btn-secondary mr-1 mb-1\"><i class=\"la la-eye\"></i></button>";
		}
		String displayActionButtons = "";
		if (!showActionButtons) {
			displayActionButtons = "display: none;";
			clickAbleTicket = " ";
		} else {
			clickAbleTicket = " onclick=\"reloadIframeChat('" + ticketBean.getTktId() + "')\" ";
		}
		String comingFromBranch = "";
		if (ticketBean.getCreatedFromSystem().equalsIgnoreCase("MAINSYSTEM")) {
			comingFromBranch = "<i class=\"fadeIn animated bx bx-git-branch\"></i>";
		}
		sb.append("<div " + dragAttributes + "  ticket-id-attr='" + ticketBean.getTktId() + "' id='ticket-id-"
				+ ticketBean.getTktId() + "' "
				+ "class=\"new-tickets-item card bg-cyan bg-darken-1 m-1\">" 
				+ "<a id='clickable-ahref-ticket-id-" + ticketBean.getTktId() + "' " + " ticket-id-attr='"
				+ ticketBean.getTktId() + "' class=\"nav-link position-relative \" " + clickAbleTicket
				+ " style='padding: 0.4rem 0.2rem;'  data-bs-toggle=\"pill\" href=\"javascript:;\">");
		if (ticketBean.getChatsInTicketNotSeen() > 0) {
			sb.append(" <span id='little-red-circle-" + ticketBean.getTktId()
					+ "'  class=\"badge badge-pill badge-danger badge-up badge-glow\" style=\"top: -10px;left: -10px;padding-left: 10px;padding-right: 10px;\">!</span>");
		}	
		
		sb.append("<div class=\"card-content\">"
		 			+ "<div class=\"card-body\" style='padding: 5px !important;'>"
		 				+ "<div class=\"row\">"
		 					+ "<div class=\"col-7\"> "
		 						+ "<h6 class='text-white mb-1'>"+ticketBean.getTktTitle()+"</h6>"
		 						+ "<h6 class='text-white'>"+ticketBean.getTktDate()+"</h6>"
		 					+ "</div>"
		 					+ "<div class='col-5 text-right'>"
		 						+ "<h6 class='text-white mb-1'><i class='la la-tag'></i>"+ticketBean.getTktId()+"</h6>"
		 						+ "<h6 class='text-white'>"+showDragButton+"</h6>"
		 					+ "</div>"
		 				+ "</div>"
//				+ "<h6 class='mb-1 font-13'>#" + ticketBean.getTktId() + comingFromBranch +"</h6>" + priority + reOpenedStatus + "</div>"
//				+ "<div class='flex-grow-1 ms-2'>" + "<h6 class='mb-1' style='font-size:12px;'>"
//				+ ticketBean.getTktTitle() + "</h6>" + "</div>"
//				+ "<div class=\"ms-2 \" style=\"margin:0px !important\">"
//				+ "<h6 class=\"mb-1\" style=\"font-size:10px;\">" + ticketBean.getTktDate() 
//				+ "</h6>" + 
//				showDragButton
				+ "</div>" + "</div>" + "</a></div>");
		return sb.toString();
	}

	public LinkedList<TicketBean> getAllNewTickets(Connection conn, int branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			newTickets = new LinkedList<>();
			pst = conn.prepareStatement(sqlTickets);
			pst.setInt(1, branchCode);
			pst.setInt(2, 0);
			pst.setString(3, "NEW");
			pst.setInt(4, branchCode);
			rs = pst.executeQuery();
			TicketBean ticketBean;
			while (rs.next()) {
				ticketBean = new TicketBean();
				ticketBean.setTktOwnerBranchName(rs.getString("branch_name")); // الفرع
				ticketBean.setTktCreatedByName(rs.getString("createdby")); // منشأ التذكرة
				ticketBean.setTktCaseId(rs.getInt("tkt_forcase")); // الى شحنه
				ticketBean.setTktId(rs.getInt("tkt_id")); // رقم التذكرة
				ticketBean.setTktDate(rs.getString("tkt_createddt")); // تاريخ ووقت الأنشاء
				ticketBean.setTktTitle(rs.getString("ticketsubject"));
				ticketBean.setTktPriorityCode(rs.getString("tkt_priority"));
				newTickets.add(ticketBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}

		return newTickets;
	}

	public LinkedList<TicketBean> getEmployeeUnderProcessTickets(Connection conn, int aBranchId, int empId)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList<TicketBean> ticketsList = new LinkedList<TicketBean>();
		try {
			pst = conn.prepareStatement(sqlTickets);
			pst.setInt(1, aBranchId);
			pst.setString(2, "OPEN");
			pst.setString(3, "OPEN");
			pst.setInt(4, empId);
			pst.setInt(5, aBranchId);
			pst.setInt(6, aBranchId);
			rs = pst.executeQuery();
			TicketBean ticketBean;
			while (rs.next()) {
				
				ticketBean = new TicketBean();
				if (aBranchId ==1) {
					ticketBean.setChatsInTicketNotSeen(rs.getInt("chat_not_seen_bymain"));
				}else {
					ticketBean.setChatsInTicketNotSeen(rs.getString("chat_seen_by_otherparty").equalsIgnoreCase("N")?1:0);
				}
				ticketBean.setTktOwnerBranchName(rs.getString("branch_name")); // الفرع
				ticketBean.setTktCreatedByName(rs.getString("createdby")); // منشأ التذكرة
				ticketBean.setTktCaseId(rs.getInt("tkt_forcase")); // الى شحنه
				ticketBean.setTktId(rs.getInt("tkt_id")); // رقم التذكرة
				ticketBean.setTktDate(rs.getString("tkt_createddt")); // تاريخ ووقت الأنشاء
				ticketBean.setTktTitle(rs.getString("ticketsubject"));
				ticketBean.setTktPriorityCode(rs.getString("tkt_priority"));
				ticketBean.setReOpened(rs.getString("tkt_reopened"));
				ticketBean.setCreatedFromSystem(rs.getString("tkt_createfromsys"));
				ticketsList.add(ticketBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}

		return ticketsList;
	}

	public LinkedList<TicketBean> getEmplpyeeTicketsClosedLessThanXDays(Connection aConn, int aBranchCode, int aEmpId,
			int aXdays) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList<TicketBean> ticketsList = new LinkedList<TicketBean>();
		try {

			pst = aConn.prepareStatement(sqlClosedXDaysTickets);
			pst.setInt(1, aEmpId);
			pst.setString(2, "CLS");
			pst.setInt(3, aXdays);
			rs = pst.executeQuery();
			TicketBean ticketBean;
			while (rs.next()) {
				ticketBean = new TicketBean();
				ticketBean.setTktOwnerBranchName(rs.getString("branch_name")); // الفرع
				ticketBean.setTktCreatedByName(rs.getString("createdby")); // منشأ التذكرة
				ticketBean.setTktCaseId(rs.getInt("tkt_forcase")); // الى شحنه
				ticketBean.setTktId(rs.getInt("tkt_id")); // رقم التذكرة
				ticketBean.setTktDate(rs.getString("tkt_createddt")); // تاريخ ووقت الأنشاء
				ticketBean.setTktTitle(rs.getString("ticketsubject"));
				ticketBean.setTktPriorityCode(rs.getString("tkt_priority"));
				ticketBean.setCreatedFromSystem(rs.getString("tkt_createfromsys"));
				ticketsList.add(ticketBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}

		return ticketsList;
	}

	public  int getChatId(Connection a_conn, int a_ticketId, int a_chatWithId, String a_chatWithRank)throws Exception{
		int chatId = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement(
					"select chat_id from p_tickets_chat where chat_ticketid=? and chat_otherpartyid=? and chat_otherpartyrank=?");
			pst.setInt(1, a_ticketId);
			pst.setInt(2, a_chatWithId);
			pst.setString(3, a_chatWithRank);
			rs = pst.executeQuery();
			if (rs.next()) {
				chatId = rs.getInt(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return chatId;
	}
	public ChatBean createChat(Connection a_conn, ChatBean a_chatBean) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int chatId = 0;
		try {
			String otherPartyChatStatus = "NEW";
			int otherPartyAssignedEmp = 0;
			chatId = getChatId(a_conn, a_chatBean.getTicketId(), a_chatBean.getChatWithId(), a_chatBean.getChatWithRank());
			if (chatId == 0) {
				if (a_chatBean.getChatStartedByBranch()!=1) {
					if (a_chatBean.getChatWithRank().equalsIgnoreCase("BRANCH")) {
						otherPartyChatStatus = "OPEN";
						otherPartyAssignedEmp = a_chatBean.getStartedById();
					}
				}
				pst = a_conn.prepareStatement("insert into p_tickets_chat"
						+ "(chat_ticketid, chat_otherpartyid, chat_otherpartyrank, chat_startedby, chat_otherpartyassigned_emp, chat_otherparty_chat_status) "
						+ "values("+ CoreUtilities.getQuestionMarks(6) + ")", Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, a_chatBean.getTicketId());
				pst.setInt(2, a_chatBean.getChatWithId());
				pst.setString(3, a_chatBean.getChatWithRank());
				pst.setInt(4, a_chatBean.getStartedById());
				pst.setInt(5, otherPartyAssignedEmp);
				pst.setString(6, otherPartyChatStatus);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				a_chatBean.setChatId(rs.getInt(1));
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}

				// update the related party we are chatting with
				if (a_chatBean.getChatWithRank().equalsIgnoreCase("DLVAGENT")) {
					pst = a_conn.prepareStatement("update p_tickets set tkt_relatedagent = ? where tkt_id =?");
					pst.setInt(1, a_chatBean.getChatWithId());
					pst.setInt(2, a_chatBean.getTicketId());
					pst.executeUpdate();
				} else if (a_chatBean.getChatWithRank().equalsIgnoreCase("MASTERCUSTOMER")
						|| a_chatBean.getChatWithRank().equalsIgnoreCase("CUSTOMER")) {
					pst = a_conn.prepareStatement(
							"update p_tickets set tkt_relatedcustomer = ? , tkt_relatedshop=? where tkt_id =?");
					pst.setInt(1, a_chatBean.getMasterCustomer());
					pst.setInt(2, a_chatBean.getChatWithRank().equalsIgnoreCase("MASTERCUSTOMER") ? 0
							: a_chatBean.getChatWithId());
					pst.setInt(3, a_chatBean.getTicketId());
					pst.executeUpdate();
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return a_chatBean;
	}

	public int saveChatMsg(Connection conn, ChatMsgBean chatMsgBean, int aBranchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int msgId = 0;
		String mainController = "N";
		String seenByOtherParty = "N";
		boolean everyThingIsOk = false;

		try {
			if (ranksThatAreChatControllers.contains(chatMsgBean.getSenderRank()) && aBranchCode == 1) {
				mainController = "Y";
			} else {
				seenByOtherParty = "Y";
			}

			pst = conn.prepareStatement("insert into p_tickets_chat_msgs "
					+ "       (cmsg_msg, cmsg_senderid, cmsg_senderrank, cmsg_mediumofcommunication, cmsg_chatid, cmsg_msgfromcontrol)"
					+ "values (?	   , ?			  , ?			   , ?						   , ?			, ?)",
					Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, chatMsgBean.getMsg());
			pst.setInt(2, chatMsgBean.getSenderId());
			pst.setString(3, chatMsgBean.getSenderRank());
			pst.setString(4, chatMsgBean.getCommunitcationMedium());
			pst.setInt(5, chatMsgBean.getChatId());
			pst.setString(6, mainController);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			msgId = rs.getInt(1);
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("update p_tickets_chat set chat_seen_bymain_system=?, chat_seen_by_otherparty=?  where chat_id=?");	
			pst.setString(1, mainController);
			pst.setString(2, seenByOtherParty);
			pst.setInt(3, chatMsgBean.getChatId());
			pst.executeUpdate();
			
			everyThingIsOk = true;
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		
		
		
		if (everyThingIsOk) {// send notification
			Notifications notifications = new Notifications();
			ChatBean chatBean = getChatInfo(conn, chatMsgBean.getChatId());
			HashMap<String, String> extraDataMap = new HashMap<String, String>();
			extraDataMap.put("ticketid", chatBean.getTicketId() + "");
			
			final ArrayList<Integer> usersList = getUsersInChat(conn, chatMsgBean.getChatId());
			
			new Thread(() -> {
				try {
					HashMap<Integer, ArrayList<String>> usersIdOneSignalIdMap = notifications
							.getOneSignalIdsPerUsers(usersList);
					notifications.sendNotificationToUser(usersIdOneSignalIdMap, chatBean.getChatWithRank(),
							"خدمة عملاء برايم | " + chatBean.getTicketId(), chatMsgBean.getMsg(), "ticketid",
							chatBean.getTicketId(), extraDataMap, aBranchCode);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}).start();

		}
		return msgId;
	}

	public LinkedList<ChatMsgBean> getChatMsgs(Connection conn, int chatId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList<ChatMsgBean> msgList = new LinkedList<ChatMsgBean>();
		try {
			pst = conn.prepareStatement(
					"select us_name, cmsg_id, cmsg_msg, cmsg_createddt, cmsg_senderid, cmsg_senderrank, cmsg_msgfromcontrol"
							+ " from p_tickets_chat_msgs join kbusers on us_id =  cmsg_senderid"
							+ " where cmsg_chatid=? order by cmsg_id ");
			pst.setInt(1, chatId);
			rs = pst.executeQuery();
			System.out.println(pst);
			ChatMsgBean cmb = new ChatMsgBean();
			while (rs.next()) {
				cmb.setMsgId(rs.getInt("cmsg_id"));
				cmb.setMsg(rs.getString("cmsg_msg"));
				cmb.setMsgDate(rs.getString("cmsg_createddt"));
				cmb.setSenderId(rs.getInt("cmsg_senderid"));
				cmb.setSenderRank(rs.getString("cmsg_senderrank"));
				cmb.setMsgFromController(rs.getString("cmsg_msgfromcontrol"));
				cmb.setSenderName(rs.getString("us_name"));
				msgList.add(cmb);
				cmb = new ChatMsgBean();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return msgList;
	}

	public ChatBean getChatInfo(Connection conn, int chatId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ChatBean chatBean = new ChatBean();
		try {
			pst = conn.prepareStatement(sqlGetChatInfo);
			pst.setInt(1, chatId);
			rs = pst.executeQuery();
			if (rs.next()) {
				// chat_ticketid, chat_otherpartyid, chat_otherpartyrank
				chatBean.setTicketId(rs.getInt("chat_ticketid"));
				chatBean.setChatWithId(rs.getInt("chat_otherpartyid"));
				chatBean.setChatWithRank(rs.getString("chat_otherpartyrank"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return chatBean;
	}

	public void setChatMesagesAsSeen(Connection conn, int chatId, int aSeenByWhichBranch) throws Exception {
		PreparedStatement pst = null;
		try {
			String sql = "update p_tickets_chat set chat_seen_bymain_system='Y' where  chat_id =?";
			if (aSeenByWhichBranch != 1) {
				 sql = "update p_tickets_chat set  chat_seen_by_otherparty='Y' where  chat_id =?";
			}
			pst = conn.prepareStatement(sql);
			pst.setInt(1, chatId);
			pst.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	private ArrayList<Integer> getUsersInChat(Connection conn, int chatId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String otherChatRank = "";
		int otherPartyId = 0;
		ArrayList<Integer> usersList = new ArrayList<>();
		try {
			pst = conn.prepareStatement(
					"select chat_otherpartyid, chat_otherpartyrank from p_tickets_chat where chat_id=? ");
			pst.setInt(1, chatId);
			rs = pst.executeQuery();
			if (rs.next()) {
				otherChatRank = rs.getString("chat_otherpartyrank");
				otherPartyId = rs.getInt("chat_otherpartyid");
			}
			if (otherChatRank.equalsIgnoreCase("DLVAGENT")) {
				usersList.add(otherPartyId);
				return usersList;
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
			if (otherChatRank.equalsIgnoreCase("MASTERCUSTOMER")) {
				pst = conn.prepareStatement(
						"select us_id from kbusers where us_rank = 'MASTERCUSTOMER' and us_mastercustid=?");
				pst.setInt(1, otherPartyId);
				rs = pst.executeQuery();
				if (rs.next()) {
					usersList.add(rs.getInt("us_id"));
				}
			} else if (otherChatRank.equalsIgnoreCase("CUSTOMER")) {
				int masterCustId = 0;
				pst = conn.prepareStatement("select cust_mastercustid from kbcustomers where cust_id = ? ");
				pst.setInt(1, otherPartyId);
				rs = pst.executeQuery();
				if (rs.next()) {
					masterCustId = rs.getInt("cust_mastercustid");
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
				pst = conn.prepareStatement("select us_id from kbusers where us_mastercustid >0 and us_mastercustid = ? "
						+ " and ( us_workingoncustomers like '%:" + otherPartyId + ":%' "
						+ " or us_workingoncustomers like '" + otherPartyId + ":%' or us_workingoncustomers is null) ");
				pst.setInt(1, masterCustId);
				rs = pst.executeQuery();
				while (rs.next()) {
					usersList.add(rs.getInt("us_id"));
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return usersList;

	}

	public int OpenNewTicket(Connection aConn, int aUserId, TicketBean ticketBean, String aRank, int aToBranch,
			int aFromBranch) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int ticketId = 0;
		int relatedCustomer = 0, relatedMasterCustomer = 0;
		try {
			// check if the cases have a ticketid
			if (ticketBean.getTktCaseId() > 0) { // ticket opened with caseid
				pst = aConn.prepareStatement(
						"select c_latest_ticketid, c_mastercustid, c_custid from p_cases where c_id = ? ");
				pst.setInt(1, ticketBean.getTktCaseId());
				rs = pst.executeQuery();
				if (rs.next()) {
					ticketId = rs.getInt("c_latest_ticketid");
					relatedCustomer = rs.getInt("c_custid");
					relatedMasterCustomer = rs.getInt("c_mastercustid");
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (ticketId > 0) {
				;// already have a ticket
			} else {
				if (ticketId == 0) {
					int assignedEmp = 0;
					String status = "NEW";
					if (aFromBranch==1) {
						assignedEmp = aUserId;
						status = "OPEN";
					}
					pst = aConn.prepareStatement("insert into p_tickets "
							+ "	(tkt_subject	, tkt_description  , tkt_priority, tkt_ownerbranch , tkt_createdby, "
							+ "	 tkt_creatortype, tkt_createfromsys, tkt_forcase , tkt_relatedbranch,tkt_assignedemp, "
							+ "  tkt_status)" + "values ("
							+ CoreUtilities.getQuestionMarks(11) + ")", Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, ticketBean.getTktTitleCode());
					pst.setString(2, ticketBean.getTktDesc());
					pst.setString(3, ticketBean.getTktPriorityCode());
					pst.setInt(4, ticketBean.getTktOwnerBranchId());
					pst.setInt(5, aUserId);
					pst.setString(6, aRank);
					pst.setString(7, "MAINSYSTEM");
					pst.setInt(8, ticketBean.getTktCaseId());
					pst.setInt(9, aToBranch);
					pst.setInt(10, assignedEmp);
					pst.setString(11, status);
					pst.executeUpdate();
					rs = pst.getGeneratedKeys();
					rs.next();
					ticketId = rs.getInt(1);
					// now update the case id
					if (ticketBean.getTktCaseId() > 0) {
						try {
							pst.close();
						} catch (Exception e) {
						}
						pst = aConn.prepareStatement("update p_cases set c_latest_ticketid=? where c_id=? ");
						pst.setInt(1, ticketId);
						pst.setInt(2, ticketBean.getTktCaseId());
						pst.executeUpdate();
					}
				}
			}
			ChatBean chatBean = new ChatBean();
			chatBean.setChatId(0);
			chatBean.setTicketId(ticketId);
			chatBean.setChatWithRank("BRANCH");

			if (aToBranch == 1) // اذا دا احجي ويه بغداد
				chatBean.setChatWithId(aFromBranch);
			else // اذا الحجي ويه بغداد
				chatBean.setChatWithId(aToBranch);

			chatBean.setStartedById(aUserId);
			chatBean.setChatStartedByBranch(aFromBranch);
			chatBean = createChat(aConn, chatBean);
			
			ChatMsgBean ticketChatMsgBean = new ChatMsgBean();
			ticketChatMsgBean.setChatId(chatBean.getChatId());
			ticketChatMsgBean.setMsg(ticketBean.getTktDesc());
			ticketChatMsgBean.setSenderRank(aRank);
			ticketChatMsgBean.setSenderId(aUserId);
			ticketChatMsgBean.setCommunitcationMedium("SYSTEM");
			saveChatMsg(aConn, ticketChatMsgBean, aFromBranch);
			/*
			 * // create relation with branch createChatRelationWithBranch(aConn, ticketId,
			 * chatBean.getChatWithId(), chatBean.getChatId(), "N", aUserId);
			 */
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return ticketId;
	}

	/*
	 * private void createChatRelationWithBranch(Connection aConn, int aTicketId,
	 * int aWithBranch, int aChatId, String aSeenbybranch, int aCreatedBy) throws
	 * Exception{ PreparedStatement pst = null; try { pst =
	 * aConn.prepareStatement("insert into p_tickets_relatedbranch" +
	 * " (trb_ticketid, trb_branchid, trb_chatid, trb_latestmsg_seenbybranch, trb_createdby) "
	 * + " values("+CoreUtilities.getQuestionMarks(5)+")"); pst.setInt(1,
	 * aTicketId); pst.setInt(2, aWithBranch); pst.setInt(3, aChatId);
	 * pst.setString(4, aSeenbybranch); pst.setInt(5, aCreatedBy);
	 * pst.executeUpdate(); }catch(Exception e) { e.printStackTrace(); throw e;
	 * }finally { try {pst.close();}catch(Exception e) {} }
	 * 
	 * }
	 */

	public LinkedList<TicketBean> getNewTickets() {
		return newTickets;
	}

	public void setNewTickets(LinkedList<TicketBean> newTickets) {
		this.newTickets = newTickets;
	}

	public LinkedList<TicketBean> getInProcessTickets() {
		return inProcessTickets;
	}

	public void setInProcessTickets(LinkedList<TicketBean> inProcessTickets) {
		this.inProcessTickets = inProcessTickets;
	}

	public LinkedList<TicketBean> getClosedTickets() {
		return closedTickets;
	}

	public void setClosedTickets(LinkedList<TicketBean> closedTickets) {
		this.closedTickets = closedTickets;
	}

}
