package com.tangenta.gkassist.school;

import com.tangenta.gkassist.common.ddd.ApplicationService;
import com.tangenta.gkassist.common.util.Basics;
import com.tangenta.gkassist.school.exception.SchoolNonExistException;
import com.tangenta.gkassist.school.model.*;
import com.tangenta.gkassist.school.repository.AdmissionGuideRepository;
import com.tangenta.gkassist.school.repository.CampusSceneryRepository;
import com.tangenta.gkassist.school.repository.SchoolBadgeRepository;
import com.tangenta.gkassist.school.repository.SchoolInfoRepository;
import com.tangenta.gkassist.school.representation.SchoolBriefRepresentation;
import com.tangenta.gkassist.school.representation.SchoolRepresentation;
import com.tangenta.gkassist.school.representation.SchoolRepresentationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class SchoolApplicationService implements ApplicationService {
    private static Logger log = LoggerFactory.getLogger(SchoolApplicationService.class);

    private final SchoolInfoRepository infoRepo;
    private final AdmissionGuideRepository admissionGuideRepo;
    private final SchoolBadgeRepository badgeRepo;
    private final CampusSceneryRepository sceneryRepo;

    public SchoolApplicationService(SchoolInfoRepository infoRepo,
                                    AdmissionGuideRepository admissionGuideRepo, SchoolBadgeRepository badgeRepo, CampusSceneryRepository sceneryRepo) {
        this.infoRepo = infoRepo;
        this.admissionGuideRepo = admissionGuideRepo;
        this.badgeRepo = badgeRepo;
        this.sceneryRepo = sceneryRepo;
    }

    public SchoolRepresentation homepage(SchoolId schoolId) {
        SchoolInfo schoolInfo = infoRepo.findById(schoolId.getSchoolId())
                .orElseThrow(() -> new SchoolNonExistException(schoolId));
        List<CampusScenery> scenery = sceneryRepo.findAllBySchoolId(schoolId);
        List<AdmissionGuide> guides = admissionGuideRepo.findAllBySchoolId(schoolId);
        Link badgeLink = Basics.nilOrTr(badgeRepo.findById(schoolId.getSchoolId()).orElse(null), x -> Link.of(x.getSchoolBadge()));

        return SchoolRepresentationService.toRepresentation(schoolInfo, scenery, guides, badgeLink);
    }

    public SchoolBriefRepresentation allSchools() {
        List<SchoolId> schoolIds = new LinkedList<>();
        List<String> schoolNames = new LinkedList<>();
        infoRepo.findAll().forEach(info -> {
            schoolIds.add(SchoolId.of(info.getSchoolId()));
            schoolNames.add(info.getName());
        });
        return new SchoolBriefRepresentation(schoolIds.size(), schoolIds, schoolNames);
    }

    public SchoolBriefRepresentation searching(String query) {
        Predicate<SchoolInfo> match = isPureDigit(query) ?
                info -> info.getSchoolId().contains(query) :
                info -> info.getName().contains(query);

        List<SchoolId> schoolIds = new LinkedList<>();
        List<String> schoolNames = new LinkedList<>();
        infoRepo.findAll().forEach(info -> {
            if (match.test(info)) {
                schoolIds.add(SchoolId.of(info.getSchoolId()));
                schoolNames.add(info.getName());
            }
        });

        return new SchoolBriefRepresentation(schoolIds.size(), schoolIds, schoolNames);
    }

    private static boolean isPureDigit(String str) {
        return str.chars().allMatch(i -> Character.isDigit((char) i));
    }
}
