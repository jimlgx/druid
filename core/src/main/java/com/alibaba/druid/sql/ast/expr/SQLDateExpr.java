/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.MySqlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLDateExpr extends SQLDateTypeExpr {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl(SQLDataType.Constants.DATE);

    public SQLDateExpr() {
        super(DATA_TYPE);
    }

    @Override
    public SQLDateExpr clone() {
        SQLDateExpr x = new SQLDateExpr();
        x.value = this.value;
        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.dataType);
        }
        visitor.endVisit(this);
    }
    public SQLDateExpr(String literal) {
        this();
        this.setValue(literal);
    }

    public SQLDateExpr(Date literal) {
        this();
        this.setValue(literal);
    }

    public SQLDateExpr(Date literal, TimeZone timeZone) {
        this();
        this.setValue(literal, timeZone);
    }

    public void setValue(String literal) {
        this.value = literal;
    }

    public void setValue(Date x) {
        setValue(x, null);
    }

    public void setValue(Date x, TimeZone timeZone) {
        if (x == null) {
            this.value = null;
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        String text = format.format(x);
        setValue(text);
    }

    public Date getDate() {
        return getDate(null);
    }

    public Date getDate(TimeZone timeZone) {
        return MySqlUtils.parseDate(getValue(), timeZone);
    }

    public boolean addDay(int delta) {
        if (value == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(value.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, delta);
            String result_chars = format.format(calendar.getTime());
            setValue(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public boolean addMonth(int delta) {
        if (value == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(value.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, delta);
            String result_chars = format.format(calendar.getTime());
            setValue(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public boolean addYear(int delta) {
        if (value == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(value.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, delta);
            String result_chars = format.format(calendar.getTime());
            setValue(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    public static boolean check(String str) {
        final int len;
        if (str == null || (len = str.length()) < 8) {
            return false;
        }

        final char c0 = str.charAt(0);
        final char c1 = str.charAt(1);
        final char c2 = str.charAt(2);
        final char c3 = str.charAt(3);
        final char c4 = str.charAt(4);
        final char c5 = str.charAt(5);
        final char c6 = str.charAt(6);
        final char c7 = str.charAt(7);

        if (c0 < '1' | c0 > '9') {
            return false;
        }
        if (c1 < '0' | c1 > '9') {
            return false;
        }
        if (c2 < '0' | c2 > '9') {
            return false;
        }
        if (c3 < '0' | c3 > '9') {
            return false;
        }

        int year = (c0 - '0') * 1000 + (c1 - '0') * 100 + (c2 - '0') * 10 + (c3 - '0');
        if (year < 1000) {
            return false;
        }

        if (c4 != '-') {
            return false;
        }

        if (c5 < '0' | c5 > '9') {
            return false;
        }

        int month, day;
        if (c6 == '-') {
            month = (c5 - '0');

            if (c7 < '0' | c7 > '9') {
                return false;
            }

            if (len == 8) {
                day = c7 - '0';
            } else if (len == 9) {
                final char c8 = str.charAt(8);
                if (c8 < '0' | c8 > '9') {
                    return false;
                }
                day = (c7 - '0') * 10 + (c8 - '0');
            } else {
                return false;
            }
        } else if (c6 < '0' | c6 > '9') {
            return false;
        } else {
            month = (c5 - '0') * 10 + (c6 - '0');

            if (c7 != '-') {
                return false;
            }

            if (len == 9) {
                final char c8 = str.charAt(8);
                if (c8 < '0' | c8 > '9') {
                    return false;
                }
                day = c8 - '0';
            } else if (len == 10) {
                final char c8 = str.charAt(8);
                final char c9 = str.charAt(9);
                if (c8 < '0' | c8 > '9') {
                    return false;
                }
                if (c9 < '0' | c9 > '9') {
                    return false;
                }
                day = (c8 - '0') * 10 + (c9 - '0');
            } else {
                return false;
            }
        }

        if (month < 1) {
            return false;
        }

        if (day < 1) {
            return false;
        }

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day > 31) {
                    return false;
                }
                return true;
            case 2:
                if (day > 29) {
                    return false;
                }
                return true;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day > 30) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public static String format(Calendar calendar) {
        if (calendar == null) {
            return null;
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        char[] chars = new char[10];
        chars[0] = (char) (year / 1000 + '0');
        chars[1] = (char) ((year / 100) % 10 + '0');
        chars[2] = (char) ((year / 10) % 10 + '0');
        chars[3] = (char) (year % 10 + '0');
        chars[4] = '-';
        chars[5] = (char) (month / 10 + '0');
        chars[6] = (char) (month % 10 + '0');
        chars[7] = '-';
        chars[8] = (char) (dayOfMonth / 10 + '0');
        chars[9] = (char) (dayOfMonth % 10 + '0');

        return new String(chars);
    }
}
