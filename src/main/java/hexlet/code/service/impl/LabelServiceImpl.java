package hexlet.code.service.impl;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label createNewLabel(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelDto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(labelDto.getName());
        return labelRepository.save(labelToUpdate);
    }
}
