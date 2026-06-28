package com.app.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;


public class Chats{
	private int ticketId;

	public LinkedList<ChatBean> getChatsList(Connection a_conn, int a_ticketId , int a_branchCode)throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList<ChatBean> chatList = new LinkedList<ChatBean>();
		try {
			String sql = "select chat_id, chat_otherpartyid, chat_createddt, chat_otherpartyrank, chat_seen_bymain_system,  "
					+ " ifnull((case when  chat_otherpartyrank = 'MASTERCUSTOMER' then ( select mcust_name from kb_mastercustomer where mcust_id =  chat_otherpartyid) "+
					"     when  chat_otherpartyrank = 'CUSTOMER' then ( select cust_name from kbcustomers where cust_id =  chat_otherpartyid) "
					+ "   when chat_otherpartyrank ='BRANCH' then (select branch_name from kbbranches where branch_id = chat_otherpartyid ) "
					+  " else  (select us_name from kbusers where us_id =  chat_otherpartyid) end),'ERROR') as chatwithname, "
					+ " ifnull((case when  chat_otherpartyrank = 'MASTERCUSTOMER' then ( select mcust_branchcode from kb_mastercustomer where mcust_id =  chat_otherpartyid) "+
					"     when  chat_otherpartyrank = 'CUSTOMER' then ( select cust_branch  from kbcustomers where cust_id =  chat_otherpartyid) "
					+ "   when chat_otherpartyrank ='BRANCH' then  chat_otherpartyid  "
					+  " else  (select us_branchcode from kbusers where us_id =  chat_otherpartyid) end),'0') as relatedbranch "
					+ "  from p_tickets_chat "
					+ "  where chat_ticketid=? order by chat_id";
			pst = a_conn.prepareStatement(sql);
			pst.setInt(1, a_ticketId);
			rs = pst.executeQuery();
			ChatBean cb = new ChatBean();
			while (rs.next()) {
				
				cb.setChatId(rs.getInt("chat_id"));
				cb.setChatStartedDate(rs.getString("chat_createddt"));
				cb.setChatWithId(rs.getInt("chat_otherpartyid"));
				cb.setChatWithRank(rs.getString("chat_otherpartyrank"));
				cb.setChatWithName(rs.getString("chatwithname"));
				//cb.setImgReplacementLetters(rs.getString("chatwithname").substring(0, 2));
				cb.setSeenByControl(rs.getString("chat_seen_bymain_system"));
				if (a_branchCode == 1 || a_branchCode == rs.getInt("relatedbranch")) {
					chatList.add(cb);
				}
				cb = new ChatBean();
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
		}
		return chatList;
	}
	
	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}
	
	
}
