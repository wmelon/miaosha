package com.xigua.miaosha.dataobject;

import java.util.Date;

public class SeckillSuccessDO extends SeckillSuccessDOKey {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.status
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    private int status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column success_killed.create_time
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.status
     *
     * @return the value of success_killed.status
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    public int getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.status
     *
     * @param status the value for success_killed.status
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column success_killed.create_time
     *
     * @return the value of success_killed.create_time
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column success_killed.create_time
     *
     * @param createTime the value for success_killed.create_time
     *
     * @mbg.generated Thu Mar 21 19:59:09 CST 2019
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}