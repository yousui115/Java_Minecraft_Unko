package yousui115.unko;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * ■EntityAIAvoidEntityを丸パｋ参考に作成
 * @author yousui
 *
 * @param <T>
 */
public class EntityAIAvoidUnko<T extends Entity> extends EntityAIBase
{
    //■
    private final Predicate<EntityLivingBase> canBeSeenSelector;

    protected EntityLiving theEntity;
    private double farSpeed;
    private double nearSpeed;
    protected T closestLivingEntity;
    private float avoidDistance;
    /** The PathEntity of our entity */
    private PathEntity entityPathEntity;
    /** The PathNavigate of our entity */
    private PathNavigate entityPathNavigate;
    private Class<T> classToAvoid;
    private Predicate <? super T > avoidTargetSelector;

    public EntityAIAvoidUnko(EntityLiving livingIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this(livingIn, classToAvoidIn, Predicates.<T>alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public EntityAIAvoidUnko(EntityLiving theEntityIn, Class<T> classToAvoidIn, Predicate <? super T > avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        this.canBeSeenSelector = new Predicate<EntityLivingBase>()
        {
            public boolean apply(EntityLivingBase target)
            {
                return target.isEntityAlive() && EntityAIAvoidUnko.this.theEntity.getEntitySenses().canSee(target);
            }
        };
        this.theEntity = theEntityIn;
        this.classToAvoid = classToAvoidIn;
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.entityPathNavigate = theEntityIn.getNavigator();
        this.setMutexBits(0xff);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        List<T> list = this.theEntity.worldObj.<T>getEntitiesWithinAABB(this.classToAvoid, this.theEntity.getEntityBoundingBox().expand((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(new Predicate[] {EntitySelectors.IS_ALIVE, this.canBeSeenSelector, this.avoidTargetSelector}));

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            this.closestLivingEntity = list.get(0);

            //■敵性Entityの立ち位置
            Vec3d vec = new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ);

            //■Entityタイプによる、処理の選別
            Vec3d vec3d;
            if (this.theEntity instanceof EntityCreature)
            {
                vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom((EntityCreature)theEntity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));
            }
            else
            {
                vec3d = findRandomTargetBlockAwayFrom(this.theEntity, 16, 2, vec);
            }

            //■
            if (vec3d == null)
            {
                return false;
            }
            else if (this.closestLivingEntity.getDistanceSq(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord) < this.closestLivingEntity.getDistanceSqToEntity(theEntity))
            {
                return false;
            }
            else
            {
                this.entityPathEntity = this.entityPathNavigate.getPathToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
                return this.entityPathEntity == null ? false : true;
                //return this.entityPathEntity == null ? false : this.entityPathEntity.isDestinationSame(vec3d);
            }
        }
    }

    /**
     * ■RandomPositionGenerator.findRandomTargetBlockAwayFrom を丸パｋ参考にした
     *
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
     * of par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    public static Vec3d findRandomTargetBlockAwayFrom(EntityLiving p_75462_0_, int p_75462_1_, int p_75462_2_, Vec3d p_75461_3_)
    {
        Vec3d staticVector = (new Vec3d(p_75462_0_.posX, p_75462_0_.posY, p_75462_0_.posZ)).subtract(p_75461_3_);

        Random random = p_75462_0_.getRNG();
        boolean flag = false;
        int k = 0;
        int l = 0;
        int i1 = 0;
        float f = -99999.0F;
        boolean flag1 = false;

        for (int l1 = 0; l1 < 10; ++l1)
        {
            int j1 = random.nextInt(2 * p_75462_1_ + 1) - p_75462_1_;
            int i2 = random.nextInt(2 * p_75462_2_ + 1) - p_75462_2_;
            int k1 = random.nextInt(2 * p_75462_1_ + 1) - p_75462_1_;

            if (staticVector == null || (double)j1 * staticVector.xCoord + (double)k1 * staticVector.zCoord >= 0.0D)
            {
                BlockPos blockpos;

/*                if (p_75462_0_.hasHome() && p_75462_1_ > 1)
                {
                    blockpos = p_75462_0_.func_180486_cf();
                    if (p_75462_0_.posX > (double)blockpos.getX())
                    {
                        j1 -= random.nextInt(p_75462_1_ / 2);
                    }
                    else
                    {
                        j1 += random.nextInt(p_75462_1_ / 2);
                    }
                    if (p_75462_0_.posZ > (double)blockpos.getZ())
                    {
                        k1 -= random.nextInt(p_75462_1_ / 2);
                    }
                    else
                    {
                        k1 += random.nextInt(p_75462_1_ / 2);
                    }
                }
*/
                j1 += MathHelper.floor_double(p_75462_0_.posX);
                i2 += MathHelper.floor_double(p_75462_0_.posY);
                k1 += MathHelper.floor_double(p_75462_0_.posZ);
                blockpos = new BlockPos(j1, i2, k1);

                if (!flag1 || true)//func_180485_d(p_75462_0_, blockpos))
                {
                    float f1 = 0.0f;//func_180484_a(p_75462_0_, blockpos);

                    if (f1 > f)
                    {
                        f = f1;
                        k = j1;
                        l = i2;
                        i1 = k1;
                        flag = true;
                    }
                }
            }
        }

        if (flag)
        {
            return new Vec3d((double)k, (double)l, (double)i1);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        List<T> list = this.theEntity.worldObj.<T>getEntitiesWithinAABB(this.classToAvoid, this.theEntity.getEntityBoundingBox().expand((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(new Predicate[] {EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector}));

        if (list.isEmpty())
        {
            return false;
        }

        return !this.entityPathNavigate.noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityPathNavigate.setPath(this.entityPathEntity, this.farSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.closestLivingEntity = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getDistanceSqToEntity(this.closestLivingEntity) < 49.0D)
        {
            this.theEntity.getNavigator().setSpeed(this.nearSpeed);
        }
        else
        {
            this.theEntity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}
