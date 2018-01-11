package com.tjstudy.expandablelistviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
//多店铺购物车实现201801111824
public class MainActivity extends AppCompatActivity {
    private ExpandableListView elv;
    private ImageView ivAllCheck;//全选按钮
    private List<Shop> shopList;
    private BaseExpandableListAdapter baseExpandableListAdapter;
    private TextView tvTotalPrice;//总价格
    private TextView tvTitle;
    private TextView tvEdit;
    private boolean mode = false;//false普通模式，true 编辑模式（删除，移入收藏夹等）
    private RelativeLayout rlNormal;
    private ImageView ivAllEditCheck;
    private TextView tvCollect;
    private TextView tvDelete;
    private RelativeLayout rlEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        //设置ExpandableView默认的分组图片标识不显示
        elv.setGroupIndicator(null);
        //设置item点击的监听器
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        MainActivity.this,
                        "点击\ngroupPosition=" + groupPosition + ";childPosition=" + childPosition,
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        });
        //设置item长按事件
        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int groupId = -1;
                int childId = -1;
                long packedPosition = elv.getExpandableListPosition(position);
                int type = ExpandableListView.getPackedPositionType(packedPosition);
                switch (type) {
                    case 0://表示当前选中项为父条目，即Group
                        groupId = ExpandableListView.getPackedPositionGroup(packedPosition);
                        break;
                    case 1://表示当前选中项为子条目
                        groupId = ExpandableListView.getPackedPositionGroup(packedPosition);//当前子条目所在的Group
                        childId = ExpandableListView.getPackedPositionChild(packedPosition);//当前子条目id
                        break;
                    default:
                        break;
                }
                Toast.makeText(
                        MainActivity.this,
                        "长按\nGroupId=" + groupId + ";childId=" + childId,
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        //普通模式全选
        ivAllCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shopList != null) {
                    //全选与反选
                    selectAll();
                }

            }
        });
        //编辑模式全选
        ivAllEditCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shopList != null) {
                    //全选与反选
                    selectAll();
                }

            }
        });
        //编辑按钮
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变当前模式
                changeMode();
            }
        });
        //删除按钮
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    /**
     * 删除购物车内选中的商品
     */
    private void delete() {
        List<Shop.Goods> dataRemove = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {
            dataRemove.clear();
            for (int k = 0; k < shopList.get(i).getGoods().size(); k++) {
                if (shopList.get(i).getGoods().get(k).isGoodsSelect()){
                    dataRemove.add(shopList.get(i).getGoods().get(k));
                }
            }
            shopList.get(i).getGoods().removeAll(dataRemove);
        }
        baseExpandableListAdapter.notifyDataSetChanged();
        //删除之后清除一下状态
        clearStatus();
    }

    /**
     * 改变当前模式（普通模式，编辑模式）
     */
    private void changeMode() {
        if (!mode) {
            //当前是普通模式,修改为编辑模式,右上角按钮变为‘完成’
            mode = true;
            tvEdit.setText("完成");
            rlEdit.setVisibility(View.VISIBLE);
            rlNormal.setVisibility(View.GONE);
        } else {
            //当前是编辑模式，修改为普通模式，右上角按钮重新变为‘编辑’
            mode = false;
            tvEdit.setText("编辑");
            rlEdit.setVisibility(View.GONE);
            rlNormal.setVisibility(View.VISIBLE);
        }
        //改变模式之后要清除之前的选中状态
        clearStatus();
    }

    /**
     * 清除选中的状态
     */
    private void clearStatus() {
            ivAllCheck.setImageResource(R.mipmap.ic_uncheck);
            ivAllEditCheck.setImageResource(R.mipmap.ic_uncheck);
            //设置全部店铺，和店铺内商品为未选中状态
            for (int j = 0; j < shopList.size(); j++) {
                shopList.get(j).setShopSelect(false);
                for (int k = 0; k < shopList.get(j).getGoods().size(); k++) {
                    shopList.get(j).getGoods().get(k).setGoodsSelect(false);
                }
            }
            baseExpandableListAdapter.notifyDataSetChanged();
            //清除状态之后重新计算一下
            calculate();
    }

    /**
     * 全选与反选
     */
    private void selectAll() {
        for (int i = 0; i < shopList.size(); i++) {
            if (!shopList.get(i).isShopSelect()) {
                //如果有一个未选中，证明当前状态是非全选，则设置为全选
                ivAllCheck.setImageResource(R.mipmap.ic_checked);
                ivAllEditCheck.setImageResource(R.mipmap.ic_checked);
                //设置全部店铺，和店铺内商品为选中状态
                for (int j = 0; j < shopList.size(); j++) {
                    shopList.get(j).setShopSelect(true);
                    for (int k = 0; k < shopList.get(j).getGoods().size(); k++) {
                        shopList.get(j).getGoods().get(k).setGoodsSelect(true);
                    }
                }
                baseExpandableListAdapter.notifyDataSetChanged();
                return;
            } else {
                if (i == shopList.size() - 1) {
                    for (int j = 0; j < shopList.size(); j++) {
                        shopList.get(j).setShopSelect(false);
                        for (int k = 0; k < shopList.get(j).getGoods().size(); k++) {
                            shopList.get(j).getGoods().get(k).setGoodsSelect(false);
                        }
                    }
                    ivAllCheck.setImageResource(R.mipmap.ic_uncheck);
                    ivAllEditCheck.setImageResource(R.mipmap.ic_uncheck);
                    baseExpandableListAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    /**
     * 计算总金额
     */
    public void calculate() {
        double total = 0.00;
        int count =0;
        for (int i = 0; i < shopList.size(); i++) {
            Shop shop = shopList.get(i);
            List<Shop.Goods> goodsList = shop.getGoods();
            for (int j = 0; j < goodsList.size(); j++) {
                Shop.Goods goods = goodsList.get(j);
                if (goods.isGoodsSelect()) {
                    if (!TextUtils.isEmpty(goods.getGoodsPrice())) {
                        total += goods.getGoodsCount() * Double.parseDouble(goods.getGoodsPrice());
                    }
                }
                count++;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            tvTotalPrice.setText("￥" + df.format(total));
        }
        tvTitle.setText("购物车(" + count + ")");
    }

    /**
     * 设置ExpandableView适配器
     */
    private void initData() {
        shopList = new ArrayList<>();
        int count = 0;//购物车内商品数量
        for (int i = 0; i < 2; i++) {
            //两个店铺
            Shop shop = new Shop();
            shop.setShopId("shop_id_" + i);
            shop.setShopName("shop_name_" + i);
            shop.setShopSelect(false);//初始设置店铺不被选中
            List<Shop.Goods> goodsList = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                //每个店铺三个商品
                Shop.Goods goods = new Shop.Goods();
                goods.setGoodsId("goods_id_" + j);
                goods.setGoodsName("goods_name_" + j);
                goods.setGoodsImgUrl("");//图片地址先不设置
                goods.setGoodsPrice(10 + j + "");
                goods.setGoodsCount(2 + j);//商品数量
                goods.setGoodsSelect(false);//初始设置商品不被选中
                goodsList.add(goods);
                count++;
            }
            shop.setGoods(goodsList);
            shopList.add(shop);
        }
        tvTitle.setText("购物车(" + count + ")");
//设置总得分组
//设置每个分组有多少个小项
//设置组的界面
//绑定数据
//店铺名称
//设置当前店铺图标为选中状态
//设置为未选中状态
//当前已选中，设置为未选中状态
//并把当前店铺下的所有商品全部设置为未选中状态
//刷新一下
//当前未选中，设置为选中状态
//并把当前店铺下的所有商品全部设置为选中状态
//刷新一下
//设置组里面的项的界面
//设置当前店铺图标为选中状态
//设置为未选中状态
//设置当前店铺图标为未选中状态
//并把当前商品的设置为未选中
//当前商品对应的店铺也设置为未选中
//设置为选中状态
//并把当前商品的设置为选中
//todo 判断是否全选了
//先设置店铺选中
//如果有一个商品未选中就设置店铺未选中
//刷新一下
//设置小项是否可以点击  一般设置都是可以点击
        baseExpandableListAdapter = new BaseExpandableListAdapter() {

            //设置总得分组
            @Override
            public int getGroupCount() {

                return shopList.size();
            }

            //设置每个分组有多少个小项
            @Override
            public int getChildrenCount(int groupPosition) {
                return shopList.get(groupPosition).getGoods().size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return shopList.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return shopList.get(groupPosition).getGoods().get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            //设置组的界面
            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                View view = View.inflate(MainActivity.this, R.layout.group_view, null);
                TextView tvGroupName = (TextView) view.findViewById(R.id.tv_group_name);
                RelativeLayout rlGroup = (RelativeLayout) view.findViewById(R.id.rl_group);

                final ImageView ivGroupCheck = (ImageView) view.findViewById(R.id.iv_group_icon);
                if (isExpanded) {
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck);
                } else {
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck);
                }
                //绑定数据
                final Shop shop = shopList.get(groupPosition);
                String shopName = shop.getShopName();//店铺名称
                tvGroupName.setText(shopName);
                if (shop.getGoods().size()==0){
                    rlGroup.setVisibility(View.GONE);
                }else{
                    rlGroup.setVisibility(View.VISIBLE);
                }
                if (shop.isShopSelect()) {
                    //设置当前店铺图标为选中状态
                    ivGroupCheck.setImageResource(R.mipmap.ic_checked);
                } else {
                    //设置为未选中状态
                    ivGroupCheck.setImageResource(R.mipmap.ic_uncheck);
                }
                //控制全选按钮的状态
                ivAllCheck.setImageResource(R.mipmap.ic_checked);
                for (int i = 0; i < shopList.size(); i++) {
                    if (!shopList.get(i).isShopSelect()) {
                        //有一个店铺未选中，则全选按钮也设置未选中
                        ivAllCheck.setImageResource(R.mipmap.ic_uncheck);
                    }
                }
                ivGroupCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (shop.isShopSelect()) {
                            //当前已选中，设置为未选中状态
                            shop.setShopSelect(false);
                            ivGroupCheck.setImageResource(R.mipmap.ic_uncheck);
                            //并把当前店铺下的所有商品全部设置为未选中状态
                            List<Shop.Goods> goods = shop.getGoods();
                            for (Shop.Goods good : goods) {
                                good.setGoodsSelect(false);
                            }
                            //刷新一下
                            notifyDataSetChanged();
                        } else {
                            //当前未选中，设置为选中状态
                            shop.setShopSelect(true);
                            ivGroupCheck.setImageResource(R.mipmap.ic_checked);
                            //并把当前店铺下的所有商品全部设置为选中状态
                            List<Shop.Goods> goods = shop.getGoods();
                            for (Shop.Goods good : goods) {
                                good.setGoodsSelect(true);
                            }
                            //刷新一下
                            notifyDataSetChanged();
                        }
                    }
                });
                return view;
            }

            //设置组里面的项的界面
            @Override
            public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                View view = View.inflate(MainActivity.this, R.layout.child_view, null);
                final ImageView ivChildCheck = (ImageView) view.findViewById(R.id.iv_child_icon);
                final TextView tvChildName = (TextView) view.findViewById(R.id.tv_child_name);
                final TextView tvChildPrice = (TextView) view.findViewById(R.id.tv_child_price);
                final View divider = view.findViewById(R.id.divider);
                final List<Shop.Goods> goodsList = shopList.get(groupPosition).getGoods();
                final Shop.Goods goods = goodsList.get(childPosition);
                tvChildPrice.setText("￥" + goods.getGoodsPrice());
                tvChildName.setText(goods.getGoodsName());
                calculate();
                if (childPosition==goodsList.size()-1){
                    divider.setVisibility(View.VISIBLE);
                }else{
                    divider.setVisibility(View.GONE);
                }

                if (goods.isGoodsSelect()) {
                    //设置当前店铺图标为选中状态
                    ivChildCheck.setImageResource(R.mipmap.ic_checked);
                } else {
                    //设置为未选中状态
                    ivChildCheck.setImageResource(R.mipmap.ic_uncheck);
                }
                ivChildCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (goods.isGoodsSelect()) {
                            //设置当前店铺图标为未选中状态
                            ivChildCheck.setImageResource(R.mipmap.ic_uncheck);
                            //并把当前商品的设置为未选中
                            goods.setGoodsSelect(false);
                            //当前商品对应的店铺也设置为未选中
                            shopList.get(groupPosition).setShopSelect(false);
                        } else {
                            //设置为选中状态
                            ivChildCheck.setImageResource(R.mipmap.ic_checked);
                            //并把当前商品的设置为选中
                            goods.setGoodsSelect(true);
                            //先设置店铺选中
                            shopList.get(groupPosition).setShopSelect(true);
                            for (int i = 0; i < goodsList.size(); i++) {
                                if (!goodsList.get(i).isGoodsSelect()) {
                                    //如果有一个商品未选中就设置店铺未选中
                                    shopList.get(groupPosition).setShopSelect(false);
                                }
                            }
                        }
                        //刷新一下
                        notifyDataSetChanged();

                    }
                });
                return view;
            }

            //设置小项是否可以点击  一般设置都是可以点击
            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }
        };
        elv.setAdapter(baseExpandableListAdapter);
        /**
         * 展开所有的组
         */
        for (int i = 0; i < 2; i++) {
            elv.expandGroup(i);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rlNormal = (RelativeLayout) findViewById(R.id.rl_normal);
        ivAllEditCheck = (ImageView) findViewById(R.id.iv_all_edit_check);
        tvCollect = (TextView) findViewById(R.id.tv_collect);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        rlEdit = (RelativeLayout) findViewById(R.id.rl_edit);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        elv = (ExpandableListView) findViewById(R.id.elv);
        ivAllCheck = (ImageView) findViewById(R.id.iv_all_check);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
    }
}
