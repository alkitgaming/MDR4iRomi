.SECONDARY:
.PRECIOUS:

.SECONDEXPANSION:
.ONESHELL:
SHELL = /usr/bin/qsh
.SHELLFLAGS = -ec

VERSION        := 0.1
COPYRIGHT      := Version $(VERSION). Copyright 2024 Scott C. Klement.
LIBRARY				 ?= SKMDR
TGTRLS         ?= v7r3m0
DEBUG					 ?= 1

ifneq (,$(BUILDLIB))
LIBRARY=$(BUILDLIB)
endif

# Make sure LIBRARY has been set and doesn't have any blanks
ifneq (1,$(words [$(LIBRARY)]))
$(error LIBRARY variable is not set correctly. Set to a valid library name and try again)
endif
ifeq (,$(LIBRARY))
$(error LIBRARY variable is not set correctly. Set to a valid library name and try again)
endif

ILIBRARY      := /qsys.lib/$(LIBRARY).lib
IPKGLIB       := /qsys.lib/$(PKGLIB).lib
RPGINCDIR     := '$(CURDIR)/src/rpg'
RPGINCDIR     := incdir($(RPGINCDIR))
BNDDIR        :=
C_OPTS				:= localetype(*localeucs2) sysifcopt(*ifsio) define(HAVE_EXPAT_CONFIG_H)
CL_OPTS       :=
RPG_OPTS      := option(*noseclvl)
PGM_OPTS      :=
OWNER         := qpgmr
USRPRF        := *user
BNDSRVPGM			:=
PGM_ACTGRP		:= *caller
SRVPGM_ACTGRP := *caller

SETLIBLIST    := liblist | grep ' USR' | while read lib type; do liblist -d $$lib; done; liblist -cd; liblist -a $(LIBRARY); liblist -a MDRST
TMPSRC        := tmpsrc
ISRCFILE      := $(ILIBRARY)/$(TMPSRC).file
SRCFILE       := srcfile($(LIBRARY)/$(TMPSRC)) srcmbr($(TMPSRC))
SRCFILE2      := $(LIBRARY)/$(TMPSRC)($(TMPSRC))
SRCFILE3      := file($(LIBRARY)/$(TMPSRC)) mbr($(TMPSRC))
PRDLIB        := $(LIBRARY)
TGTCCSID      := *job
DEVELOPER     ?= $(USER)
MAKE          := make
LOGFILE       = $(CURDIR)/tmp/$(@F).txt
OUTPUT        = >$(LOGFILE) 2>&1

# Remove compile listings from previous `make`
$(shell test -d $(CURDIR)/tmp || mkdir $(CURDIR)/tmp; rm $(CURDIR)/tmp/*.txt >/dev/null 2>&1)

#
# Set variables for adding in a debugging view if desired
#

ifeq ($(DEBUG), 1)
	DEBUG_OPTS     := dbgview(*list)
	SQL_DEBUG_OPTS := dbgview(*source)
	CPP_OPTS       := $(CPP_OPTS) output(*print)
else
	DEBUG_OPTS     := dbgview(*none)
	SQL_DEBUG_OPTS := dbgview(*none)
	CPP_OPTS       := $(CPP_OPTS) optimize(40) output(*none)
	RPG_OPTS       := $(RPG_OPTS) optimize(*full)
endif

define FILEOBJS
	LOCMAST.file HISTLOG.file ROBOPATHD.file 
endef
FILEOBJS := $(addprefix $(ILIBRARY)/, $(FILEOBJS))

define TARGETS
	ROMIPATH.pgm ROMILOGR.pgm ROBOPATHR.pgm
endef	
TARGETS := $(addprefix $(ILIBRARY)/, $(TARGETS)) 

# FTPAPIR4.srvpgm_deps := $(addprefix $(ILIBRARY)/, FTPAPIR4.module FTPTCP.module)

.PHONY: all clean release

all: $(TARGETS) | $(FILEOBJS) 

clean:
	rm -rf $(ISRCFILE) $(TARGETS) $(ILIBRARY)/*.MODULE
	system -v "dltf file($(LIBRARY)/ROBOPATHD)" || true
	system -v "dltf file($(LIBRARY)/LOCMAST)" || true
	system -v "dltf file($(LIBRARY)/HISTLOG)" || true
	rm -rf $(FILEOBJS) build
	rm -f src/rpg/VERSION.rpgleinc

$(ILIBRARY): | tmp
	-system -v 'crtlib lib($(LIBRARY)) type(*PROD)'
	system -v "chgobjown obj($(LIBRARY)) objtype(*lib) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)) objtype(*lib) user(*public) aut(*use) replace(*yes)"

$(ISRCFILE): | $(ILIBRARY)
	-system -v 'crtsrcpf rcdlen(112) $(SRCFILE3)'

tmp:
	mkdir $(CURDIR)/tmp	

#
#  Specific rules for objects that don't follow the "cookbook" rules, below.
#

src/rpg/VERSION.rpgleinc:
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	(rm -f '$(@)'
	touch -C 819 '$(@)'
	echo "     H COPYRIGHT('$(COPYRIGHT) +" >> '$(@)'
	echo "     H All rights reserved. A member called LICENSE was included +" >> '$(@)'
	echo "     H with this distribution and contains important license +" >> '$(@)'
	echo "     H information.')" >> '$(@)') $(OUTPUT)

#
#  Standard "cookbook" recipes for building objects
#
$(ILIBRARY)/%.module: %.clle | $(ISRCFILE) $$($$*.module_files) $$($$*.module_spgms)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	system -v "crtclmod module($(LIBRARY)/$(*F)) $(SRCFILE) $(CL_OPTS) tgtrls($(TGTRLS)) $(DEBUG_OPTS)") $(OUTPUT)
	
$(ILIBRARY)/%.module: %.rpgle $$($$*.module_deps) | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	system -v "crtrpgmod module($(LIBRARY)/$(*F)) $(SRCFILE) $(RPGINCDIR) $(RPG_OPTS) tgtrls($(TGTRLS)) $(DEBUG_OPTS)") $(OUTPUT)
	
$(ILIBRARY)/%.module: %.sqlrpgle $$($$*.module_deps) | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	system -v "crtsqlrpgi obj($(LIBRARY)/$(*F)) $(SRCFILE) compileopt('$(subst ','',$(RPGINCDIR)) $(subst ','',$(RPG_OPTS))') $(SQL_OPTS) tgtrls($(TGTRLS)) $(SQL_DEBUG_OPTS) objtype(*module) rpgppopt(*lvl2)") $(OUTPUT)
	
$(ILIBRARY)/%.pnlgrp: %.pnlgrp | $$($$*.pnlgrp_deps) $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	system -v "crtpnlgrp pnlgrp($(LIBRARY)/$(*F)) $(SRCFILE)"
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)

$(ILIBRARY)/%.cmd: %.cmd $$($$*.cmd_deps) | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	system -v 'crtcmd cmd($(LIBRARY)/$(*F)) $(SRCFILE) pgm(*libl/$(*F)) prdlib($(PRDLIB))'
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)

$(ILIBRARY)/%.pgm: $$($$*.pgm_deps) $(ILIBRARY)/%.module | $(FILEOBJS)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	($(SETLIBLIST)
	system -v 'dltpgm pgm($(LIBRARY)/$(*F))' || true
	system -v 'crtpgm pgm($(LIBRARY)/$(*F)) module($(foreach MODULE, $(notdir $(filter %.module, $(^))), ($(LIBRARY)/$(basename $(MODULE))))) entmod(*pgm) $(PGM_OPTS) actgrp($(PGM_ACTGRP)) tgtrls($(TGTRLS)) bndsrvpgm($(foreach SRVPGM, $(notdir $(filter %.srvpgm, $(|))), ($(basename $(SRVPGM))))) $(BNDDIR) $($(@F)_opts) usrprf($(USRPRF))'
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)
			
$(ILIBRARY)/%.srvpgm: src/srv/%.bnd $$($$*.srvpgm_deps) | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	(rm -rf '$(@)'
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	$(SETLIBLIST)
	system -v 'dltsrvpgm srvpgm($(LIBRARY)/$(*F))' || true
	system -v 'crtsrvpgm srvpgm($(LIBRARY)/$(*F)) module($(foreach MODULE, $(notdir $(filter %.module, $(^))), ($(LIBRARY)/$(basename $(MODULE))))) $(SRCFILE) $(PGM_OPTS) actgrp($(SRVPGM_ACTGRP)) tgtrls($(TGTRLS)) bndsrvpgm($(foreach SRVPGM, $(notdir $(filter %.srvpgm, $(^))), ($(basename $(SRVPGM))))) $($(@F)_opts) $(BNDDIR) usrprf($(USRPRF))'
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)

$(ILIBRARY)/%.file: %.dspf | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	(rm -rf '$(@)'
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	$(SETLIBLIST)
	system -v 'crtdspf file($(LIBRARY)/$(*F)) $(SRCFILE)'
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)

$(ILIBRARY)/%.file: %.sql | $(ISRCFILE)
	@$(info Creating $(@))touch -C 1208 $(LOGFILE)
	(rm -rf '$(@)'
	cat '$(<)' | Rfile -wQ '$(SRCFILE2)'
	$(SETLIBLIST)
	system -v 'runsqlstm $(SRCFILE) commit(*none) dftrdbcol($(LIBRARY))'
	system -v "chgobjown obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) newown($(OWNER)) curownaut(*revoke)"
	system -v "grtobjaut obj($(LIBRARY)/$(basename $(@F))) objtype(*$(subst .,,$(suffix $(@F)))) user(*public) aut(*use) replace(*yes)") $(OUTPUT)
