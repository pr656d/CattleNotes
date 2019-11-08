package com.pr656d.cattlenotes.shared.utils.common

import com.pr656d.cattlenotes.data.local.db.CattleEntity
import com.pr656d.cattlenotes.model.Cattle

object LoadSampleData {
    fun getListOfCattleEntity() =
        arrayListOf<CattleEntity>().apply {
            add(CattleEntity("764538726128", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.CALF_FEMALE.displayName))
            add(CattleEntity("972349683764", "Sita", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("098386547654", "Parvati", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("981265127629", type = Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("987908907398", "Janu", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("256347889780", "Lakshmi", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("786576523465", type = Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("785323476563", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("908767862267", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("764538726128", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.CALF_FEMALE.displayName))
            add(CattleEntity("972349683764", "Sita", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("098386547654", "Parvati", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("981265127629", type = Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("987908907398", "Janu", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("256347889780", "Lakshmi", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("786576523465", type = Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
            add(CattleEntity("785323476563", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.MILKING.displayName))
            add(CattleEntity("908767862267", "Janki", Cattle.CattleType.COW.displayName, group = Cattle.CattleGroup.DRY.displayName))
        }
}
