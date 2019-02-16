package com.welltou.btprintdemo.BOLUTEKBLE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maitao on 2017/10/18.
 */

public class PrintTest {

    //复位打印机
    public static final byte[] RESET = {27, '@'};
    //退纸
    public static final byte[] OUT = {0x0c};
    // 设定汉字4倍角打印模式
    public static final byte[] FONT_FOUR_DOUBLE = {28, 87, 1};
    //解除汉字4倍角打印模式
    public static final byte[] FONT_FOUR_DOUBLE_CANCEL = {28, 87, 0};
    // 打印并换行（水平定位）
    public static final byte[] LF = {0x0A};
    //行间距-设定1/8英寸行间距
    public static final byte[] VERTICAL_SPACING_0 = {27, 48};

    // 纸张大小设置-定义单位
    public static final byte[] PAPER_UNIT = {27, 40, 85, 1, 0, 60};
    // 纸张大小设置-页长-按照定义单位设置页长
    public static final byte[] PAPER_LENGHT_1 = {27, 40, 67, 2, 0, 74, 01};

    private static OutputStream outputStream = null;
    private OutputStreamWriter mWriter = null;

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * 设置光标的绝对X位置
     */
    public static final byte[] XL1 = {0x1b, 0x24, 0x00, 0x00};
    public static final byte[] XL2 = {0x1b, 0x24, (byte)0x2c, 0x01};
    public static final byte[] XL3 = {0x1b, 0x24, (byte)0x2c, 0x01};
    public static final byte[] XL4 = {0x1b, 0x24, (byte)0x2c, 0x01};
    public static final byte[] XL5 = {0x1b, 0x24, 0x3c, 0x01};

    /**
     * 设置光标的绝对Y位置
     */
    public static final byte[] YL1 = {0x1b, 0x28, 0x56, 0x02, 0x00, 0x3c, 0x00};
    public static final byte[] YL2 = {0x1b, 0x28, 0x56, 0x02, 0x00, 0x00, 0x00};
    public static final byte[] YL3 = {0x1b, 0x28, 0x56, 0x02, 0x00, 0x5a, 0x00};
    public static final byte[] YL4 = {0x1b, 0x28, 0x56, 0x02, 0x00, 0x78, 0x00};
    public static final byte[] YL5 = {0x1b, 0x28, 0x56, 0x02, 0x00, 0x08, 0x01};

    public PrintTest(OutputStream outputStream, String encoding) throws IOException {
        mWriter = new OutputStreamWriter(outputStream, encoding);
        PrintTest.outputStream = outputStream;
    }

    public void CloseIS(){
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print(BluetoothSocket bluetoothSocket, String sendText, Bitmap bitmap) {

        try {
            if("表格".equals(sendText)){


                print1(bluetoothSocket, bitmap);



            } else if("图片".equals(sendText)){
                PrintTest pc = new PrintTest(bluetoothSocket.getOutputStream(), "GBK");


                pc.selectCommand(RESET);

                pc.selectCommand(XL5);
                pc.selectCommand(YL5);
                new PrintImg().printBitmap(pc, bitmap);
            }else {

                PrintTest pc = new PrintTest(bluetoothSocket.getOutputStream(), "GBK");
                pc.selectCommand(RESET);
                pc.selectCommand(PAPER_UNIT);//定义单位
                pc.selectCommand(PAPER_LENGHT_1);//设置页长
                pc.selectCommand(FONT_FOUR_DOUBLE);
                pc.printText("标题\n\n");
                pc.selectCommand(FONT_FOUR_DOUBLE_CANCEL);

                pc.printText("第一行");
                pc.selectCommand(LF);
                pc.printText("第二行");
                pc.selectCommand(LF);

                pc.selectCommand(VERTICAL_SPACING_0);
                pc.printText("第三行");
                pc.selectCommand(LF);
                pc.printText("第四行"+ sendText);
                pc.selectCommand(OUT);


//            pc.CloseIS();
                print1(bluetoothSocket, bitmap);
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void print1(BluetoothSocket bluetoothSocket, Bitmap bitmap) {
        try {
            PrintTest pc = new PrintTest(bluetoothSocket.getOutputStream(), "GBK");
            pc.selectCommand(RESET);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("heading", "深圳市微舵科技有限公司");
            map.put("subheading", "出库单");
            map.put("serviceCall", "0755-55555555");
            map.put("contactName", "深圳市宝安区");
            map.put("contactCall", "13600000000");
            map.put("billDate", "2017-11-16");
            map.put("customerAddr", "");
            map.put("saler", "杨R");
            map.put("billNumber", "XS000000000002");
            map.put("totalAmount", "4500.00");
            map.put("customerCommitment", "");
            map.put("collection", "");
            map.put("debt", "4500.00");
            map.put("companyAddr", "深圳市宝安区");
            map.put("originator", "小程");
            map.put("finance", "");
            map.put("consigner", "");
            map.put("consignee", "");
            map.put("entryTotalNumber", "43.00");
            map.put("entryTotalAmount", "4500.00");
            map.put("entryTotalAmount_1", "肆仟伍佰元整");

            map.put("billEntryLength", 104);
            map.put("billEntryColumnWidthRatio", new Integer[]{6, 14, 14, 14, 8, 8, 8, 12, 8, 12});

            List<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"序号", "条码", "商品", "规则", "单位", "数量", "单价", "销售金额", "出库仓", "备注"});//表头
            list.add(new String[]{"", "", "", "", "合计", "43.00", "", "4500.00", "", ""});//合计
            list.add(new String[]{"1", "6935600100215", "AAAAAAA", "测试200g*25", "件", "20.00", "80", "1，600.00", "成品仓", ""});
            list.add(new String[]{"2", "6935600100215", "BBBBBBB", "测试200g*25", "件", "2.00", "0", "", "成品仓", "送一件"});
            list.add(new String[]{"3", "6935600100228", "CCCCCCC", "测试200g*25包", "件", "20.00", "145", "2，900.00", "成品仓", "换一件"});
            map.put("billEntry", list);

            printHead(pc, map, bitmap);
            printEntry(pc, map);
            printFooter(pc, map);
            pc.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printFooter(PrintTest pc, Map<String, Object> map) {
        pc.printTextWithLB("备注：");
        pc.selectCommand(LF);
        pc.selectCommand(LF);
        pc.printTextWithLB("公司地址：" + map.get("companyAddr"));
        pc.selectCommand(LF);
        pc.selectCommand(OUT);
    }

    /**
     * 设置光标的绝对X位置
     */
    private static void printEntry(PrintTest pc, Map<String, Object> map) {

        List<String[]> list = (List<String[]>) map.get("billEntry");
        Integer[] columnWidthRatio = (Integer[]) map.get("billEntryColumnWidthRatio");

        String start =  "┌──┬──────┬──────┬──────┬───┬───┬───┬─────┬───┬─────┐";
        String last_3 = "│                                              │      │      │      │          │      │          │";
        String mid ="├──┼──────┼──────┼──────┼───┼───┼───┼─────┼───┼─────┤";
        String last ="└───────────────────────────────────────────────────┘";
        String col = "│    │            │            │            │      │      │      │          │      │          │";
        String end=  "├──┴──────┴──────┴──────┼───┼───┼───┼─────┼───┼─────┤";
        String last_1= "│                                                                                                      │";
        String last_2="├───────────────────────┴───┴───┴───┴─────┴───┴─────┤";

        pc.selectCommand(XL1);
        pc.selectCommand(new byte[]{0x1c, 0x76, 0x01}); //连线
        pc.printText(start + "\n");
        pc.selectCommand(XL1);
        pc.printText(col);
        pc.selectCommand(XL1);
        pc.printText(getTextCenter(columnWidthRatio, list.get(0)) + "\n");

        for (int i = 2; i < list.size(); i++) {
            pc.printText(mid + "\n");
            pc.printText(col);
            pc.selectCommand(XL1);
            pc.printText(getTextLeft(columnWidthRatio, list.get(i)) + "\n");
        }


        pc.printText(end + "\n");
        pc.printText(last_3);
        pc.selectCommand(XL1);
        pc.printText(getTextLeft(columnWidthRatio, list.get(1)) + "\n");

        pc.printText(last_2+ "\n");
        pc.selectCommand(XL1);
        pc.printText(last_1);
        pc.selectCommand(XL1);
        pc.printText("   合计 金额大写：" + map.get("entryTotalAmount_1") + "   \n");
        pc.printText(last+ "\n");

    }


    private static String getTextCenter(Integer[] columnWidthRatio, String[] ss) {
        StringBuffer sb = new StringBuffer(" ");
        for (Integer i = 0; i < columnWidthRatio.length; i++) {
            int textLength = PrintTest.getBytesLength(ss[i]);
            int m = (columnWidthRatio[i] - textLength) / 2;
            int n = (columnWidthRatio[i] - textLength) % 2;

            for (int j = 0; j < m; j++) {
                sb.append(" ");
            }
            sb.append(ss[i]);
            for (int j = 0; j < m; j++) {
                sb.append(" ");
            }
            if (n == 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static String getTextLeft(Integer[] columnWidthRatio, String[] ss) {
        StringBuffer sb = new StringBuffer(" ");
        for (Integer i = 0; i < columnWidthRatio.length; i++) {
            if (i == 0) {//序号居中
                int textLength = PrintTest.getBytesLength(ss[i]);
                int m = (columnWidthRatio[i] - textLength) / 2;
                int n = (columnWidthRatio[i] - textLength) % 2;

                for (int j = 0; j < m; j++) {
                    sb.append(" ");
                }
                sb.append(ss[i]);
                for (int j = 0; j < m; j++) {
                    sb.append(" ");
                }
                if (n == 1) {
                    sb.append(" ");
                }
            } else {
                int textLength = PrintTest.getBytesLength(ss[i]);
                sb.append(ss[i]);
                for (int j = 0; j < columnWidthRatio[i] - textLength; j++) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 初始化单位
     */
    public static final byte[] BX = {0x1b, 0x28, 0x55, 0x01, 0x00, 0x3c};
    private static void printHead(PrintTest pc, Map<String, Object> map, Bitmap bitmap) {
        pc.selectCommand(FONT_FOUR_DOUBLE);//设定汉字四倍角打印模式
        pc.printTextWithLB(pc.printData((String) map.get("heading"), 1));
        pc.selectCommand(LF);
        pc.printTextWithLB(pc.printData((String) map.get("subheading"), 1));
        pc.selectCommand(FONT_FOUR_DOUBLE_CANCEL);//解除汉字四倍角打印模式
        try {
            pc.selectCommand(XL5);
            pc.selectCommand(YL2);
            new PrintImg().printBitmap(pc, bitmap);
        } catch (Exception e){
            e.printStackTrace();
        }
        pc.printText("服务电话：" + map.get("serviceCall"));
        pc.selectCommand(LF);
        pc.printTextWithLB(pc.printThreeData("客户名称：" + map.get("contactName"), "联系电话：" + map.get("contactCall"), "单据日期：2017-11-16" + map.get("billDate")));
        pc.printTextWithLB(pc.printThreeData("客户地址" + map.get("customerAddr"), "销售人员：" + map.get("saler"), "单据编号：" + map.get("billNumber")));
        pc.selectCommand(BX);//设置
        pc.selectCommand(new byte[]{0x1b, 0x30});//行间距
    }

    /**
     * 打印文字
     *
     * @param text 要打印的文字(带换行)
     */
    public static void printTextWithLB(String text) {
        try {
            text = text + "\n";
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            //Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 打印两列
     *
     * @param text  打印文字
     * @param align 0：左对齐 1：中间对齐 2：右对齐
     * @return
     */
    /**
     * 打印纸一行最大的字节
     */
    public static int LINE_BYTE_SIZE = 100;
    public static int LEFT_LENGTH = 50;
    public static int RIGHT_LENGTH = 50;
    /**
     * 左侧汉字最多显示几个文字
     */
    public static int LEFT_TEXT_MAX_LENGTH = 33;
    @SuppressLint("NewApi")
    public static String printData(String text, int align) {
        StringBuilder sb = new StringBuilder();

        int textLength = PrintTest.getBytesLength(text) * 2;
        int length = 0;
        if (align == 1) {
            length = (LINE_BYTE_SIZE - textLength) / 2;
        } else if (align == 2) {
            length = (LINE_BYTE_SIZE - textLength);
        }
        for (int i = 0; i < length / 2; i++) {
            sb.append(" ");
        }
        sb.append(text);

        return sb.toString();
    }

    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String printThreeData(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = PrintTest.getBytesLength(leftText);
        int middleTextLength = PrintTest.getBytesLength(middleText);
        int rightTextLength = PrintTest.getBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    public static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public static void selectCommand(byte[] command) {
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public static void printText(String text) {
        try {
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void finish(){
        try{
            outputStream.close();
            outputStream = null;
        } catch (IOException e){

        }
    }

    public static void  printRawBytes(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        outputStream.flush();
    }
}
